package com.itbaizhan.shopping_search_service.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.shopping_common.pojo.*;
import com.itbaizhan.shopping_common.service.SearchService;
import com.itbaizhan.shopping_search_service.repository.GoodsEsRepository;
import lombok.SneakyThrows;
import org.apache.dubbo.config.annotation.DubboService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.AnalyzeRequest;
import org.elasticsearch.client.indices.AnalyzeResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.util.StringUtils;


import java.util.*;
import java.util.stream.Collectors;

@DubboService
//@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private GoodsEsRepository goodsEsRepository;

    @Autowired
    private ElasticsearchRestTemplate template;

    // 监听同步消息队列
    @RabbitListener(queues = "sync_goods_queue")
    public void listenerQueue1(GoodsDesc goodsDesc){
        syncGoodsToEs(goodsDesc);
    }
//    // 监听删除消息队列
    @RabbitListener(queues = "del_goods_queue")
    public void listenerQueue2(Long id){
        delete(id);
    }

    // 分词
    @SneakyThrows
    public List<String> analyze(String text,String analyzer){
        AnalyzeRequest request = AnalyzeRequest.withIndexAnalyzer("goods", analyzer, text);
        AnalyzeResponse response = restHighLevelClient.indices().analyze(request, RequestOptions.DEFAULT);
        List<String> words = new ArrayList();
        List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
        for (AnalyzeResponse.AnalyzeToken token : tokens) {
            String term = token.getTerm();
            words.add(term);
        }
        return words;
    }

    @Override
    public List<String> autoSuggest(String keyword) {
        // 创建补齐请求
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        SuggestionBuilder suggestionBuilder = SuggestBuilders
                .completionSuggestion("tags") // 要查找的域
                .prefix(keyword)
                .skipDuplicates(true) // 自动去重
                .size(10);// 显示10条数据
        suggestBuilder.addSuggestion("prefix_suggestion",suggestionBuilder);
        // 发送请求
        SearchResponse response = template.suggest(suggestBuilder, IndexCoordinates.of("goods"));
        // 处理结果
        List<String> list = response.getSuggest()
                .getSuggestion("prefix_suggestion")
                .getEntries()
                .get(0)
                .getOptions()
                .stream()
                .map(Suggest.Suggestion.Entry.Option::getText)
                .map(Text::toString)
                .collect(Collectors.toList());
        return list;
    }

    @Override
    public GoodsSearchResult search(GoodsSearchParam goodsSearchParam) {
        // 1.构造ES搜索条件
        NativeSearchQuery query = buildQuery(goodsSearchParam);
        // 2.搜索
        SearchHits<GoodsES> search = template.search(query, GoodsES.class);
        // 3.将查询结果封装为Page对象
        // 3.将查询到的结果封装为list对象
        List<GoodsES> list = new ArrayList();
        for (SearchHit<GoodsES> goodsESSearchHit : search) {
            GoodsES goodsES = goodsESSearchHit.getContent();
            list.add(goodsES);
        }
        Page<GoodsES> page = new Page();
        page.setCurrent(goodsSearchParam.getPage())
                .setSize(goodsSearchParam.getSize())
                .setTotal(search.getTotalHits())
                .setRecords(list);
        // 4.封装结果对象
        GoodsSearchResult goodsSearchResult = new GoodsSearchResult();
        // 4.1 查询结果
        goodsSearchResult.setGoodsPage(page); // 页面商品信息
        // 4.2 查询查询参数
        goodsSearchResult.setGoodsSearchParam(goodsSearchParam); // 搜索条件回显
        // 4.3 查询面板
        buildSearchPinel(goodsSearchParam,goodsSearchResult);
        return goodsSearchResult;
    }

    /**
     * 构造搜索条件
     * @param goodsSearchParam 搜索条件对象
     * @return
     */
    public NativeSearchQuery buildQuery(GoodsSearchParam goodsSearchParam){
        // 创建复杂查询条件对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 如果查询条件有关键字，关键字可以匹配商品名、品牌、副标题，否则查询所有
        if(!StringUtils.hasText(goodsSearchParam.getKeyword())){
            // 查询所有
            MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
            boolQueryBuilder.must(matchAllQueryBuilder);
        }else{
            String keyword = goodsSearchParam.getKeyword();
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, "goodsName", "caption", "brand"); // 先分词在查询
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }
        // 如果查询条件有品牌，则精确匹配品牌
        if(StringUtils.hasText(goodsSearchParam.getBrand())){
            TermQueryBuilder termQuery = QueryBuilders.termQuery("brand", goodsSearchParam.getBrand());
            boolQueryBuilder.must(termQuery);
        }
        // 如果查询条件有价格则匹配价格
        Double highPrice = goodsSearchParam.getHighPrice(); // 最高价
        Double lowPrice = goodsSearchParam.getLowPrice(); // 最低价
        if(highPrice != null && highPrice > 0){ // 小于最高价
            RangeQueryBuilder lte = QueryBuilders.rangeQuery("price").lte(highPrice);
            boolQueryBuilder.must(lte);
        }
        if(lowPrice != null && lowPrice > 0){ // 大于最高价
            RangeQueryBuilder gte = QueryBuilders.rangeQuery("price").gte(lowPrice);
            boolQueryBuilder.must(gte);
        }
        // 如果查询条件有规格项则精确匹配规格项
        Map<String, String> specificationOptions = goodsSearchParam.getSpecificationOption();
        if(specificationOptions != null && specificationOptions.size() > 0){
            Set<Map.Entry<String, String>> entries = specificationOptions.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                String key = entry.getKey();
                String value = entry.getValue();
                if(StringUtils.hasText(key)){
                    TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("specification." + key + ".keyword", value);
                    boolQueryBuilder.must(termQueryBuilder);
                }
            }
        }
        // 添加分页条件
        // ES的分页查询页数是从0开始的 MP是从1开始的
        PageRequest request = PageRequest.of(goodsSearchParam.getPage() - 1, goodsSearchParam.getSize());
        // 查询构造器，构造查询条件以及分页条件、排序条件
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder)
                                .withPageable(request);
        // 如果查询条件有排序则添加排序条件
        String sortFiled = goodsSearchParam.getSortFiled(); // 排序字段
        String sort = goodsSearchParam.getSort(); // 排序方式
        SortBuilder sortBuilder = null;
        if(StringUtils.hasText(sortFiled) && StringUtils.hasText(sort)){
            sortBuilder = SortBuilders.fieldSort("id");
            if(sortFiled.equals("NEW")){ // 新品的正序是id的倒序
                if(sort.equals("ASC")){
                    sortBuilder.order(SortOrder.DESC);
                }
                if(sort.equals("DESC")){
                    sortBuilder.order(SortOrder.ASC);
                }
            }
            if(sortFiled.equals("PRICE")){
                sortBuilder = SortBuilders.fieldSort("price");
                if(sort.equals("ASC")){
                    sortBuilder.order(SortOrder.ASC);
                }
                if(sort.equals("DESC")){
                    sortBuilder.order(SortOrder.DESC);
                }

            }
            nativeSearchQueryBuilder.withSorts(sortBuilder);
        }
        NativeSearchQuery build = nativeSearchQueryBuilder.build();
        return build;
    }

    /**
     * 封装查询面板
     * 根据关联度查询出前20条数据
     * @param goodsSearchParam 搜索条件
     * @param goodsSearchResult 搜索结果
     */
    public void buildSearchPinel(GoodsSearchParam goodsSearchParam,GoodsSearchResult goodsSearchResult){
        // 构造搜索条件
        goodsSearchParam.setPage(1);
        goodsSearchParam.setSize(20);
        // es默认排序方式就是以关联度排序的
        goodsSearchParam.setSortFiled(null);
        goodsSearchParam.setSort(null);
        NativeSearchQuery query = buildQuery(goodsSearchParam);
        // 搜索
        SearchHits<GoodsES> content = template.search(query, GoodsES.class);
        // 将搜索到的结果集封装为一个list集合
        List<GoodsES> list = new ArrayList();
        for (SearchHit<GoodsES> searchHit : content) {
            GoodsES goodsES = searchHit.getContent();
            list.add(goodsES);
        }
        // 品牌列表
        Set<String> brands = new HashSet();
        // 类别列表
        Set<String> productTypes = new HashSet();
        // 规格列表
        Map<String, Set<String>> specifications = new HashMap();
        for (GoodsES goodsES : list) {
            brands.add(goodsES.getBrand()); // 品牌列表数据
            List<String> productType = goodsES.getProductType();
            productTypes.addAll(productType);
            Map<String, List<String>> specification = goodsES.getSpecification();
            Set<Map.Entry<String, List<String>>> entries = specification.entrySet();
            for (Map.Entry<String, List<String>> entry : entries) {
                String key = entry.getKey();
                List<String> value = entry.getValue();
                // 如果没有遍历出该规格新增键值对，如果已经遍历出规格则向规格中添加规格项
                if(!specifications.containsKey(key)){
                    specifications.put(key,new HashSet(value));
                }else{
                    specifications.get(key).addAll(value);
                }
            }
        }
        goodsSearchResult.setBrands(brands);
        goodsSearchResult.setProductType(productTypes);
        goodsSearchResult.setSpecifications(specifications);

    }

    @Override
    public void syncGoodsToEs(GoodsDesc goodsDesc) {
        // 将GoodsDesc对象转为GoodsEs
        GoodsES goodsES = new GoodsES();
        goodsES.setId(goodsDesc.getId());
        goodsES.setGoodsName(goodsDesc.getGoodsName());
        goodsES.setCaption(goodsDesc.getCaption());
        goodsES.setPrice(goodsDesc.getPrice());
        goodsES.setHeaderPic(goodsDesc.getHeaderPic());
        goodsES.setBrand(goodsDesc.getBrand().getName());
        List<String> productName = new ArrayList();
        productName.add(goodsDesc.getProductType1().getName());
        productName.add(goodsDesc.getProductType2().getName());
        productName.add(goodsDesc.getProductType3().getName());
        goodsES.setProductType(productName);
        Map<String,List<String>> map = new HashMap();
        List<Specification> specifications = goodsDesc.getSpecifications();
        for (Specification specification : specifications) {
            List<SpecificationOption> specificationOptions = specification.getSpecificationOptions();
            List<String> options = new ArrayList();
            for (SpecificationOption specificationOption : specificationOptions) {
                options.add(specificationOption.getOptionName());
            }
            map.put(specification.getSpecName(), options);
        }
        goodsES.setSpecification(map);
        List<String> tags = new ArrayList();
        tags.add(goodsDesc.getBrand().getName()); // 品牌名是关键字
        tags.addAll(analyze(goodsDesc.getGoodsName(),"ik_smart")); // 商品名分词后是关键字
        tags.addAll(analyze(goodsDesc.getCaption(),"ik_smart")); // 副标题分词后是关键字
        goodsES.setTags(tags);
        goodsEsRepository.save(goodsES);
    }

    @Override
    public void delete(Long id) {
        goodsEsRepository.deleteById(id);
    }
}
