package com.itbaizhan.shopping_goods_service.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.shopping_common.pojo.*;
import com.itbaizhan.shopping_common.service.GoodsService;
import com.itbaizhan.shopping_common.service.SearchService;
import com.itbaizhan.shopping_goods_service.mapper.GoodsImageMapper;
import com.itbaizhan.shopping_goods_service.mapper.GoodsMapper;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@DubboService
@Transactional
//@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsImageMapper goodsImageMapper;
//    @DubboReference
//    private SearchService searchService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Override
    public void add(Goods goods) {
        // 新增商品数据
        goodsMapper.insert(goods);
        // 新增商品图片数据
        Long goodsId = goods.getId();
        List<GoodsImage> images = goods.getImages();
        for (GoodsImage image : images) {
            image.setGoodsId(goodsId);
            goodsImageMapper.insert(image);
        }
        // 新增商品规格项数据
        List<Specification> specifications = goods.getSpecifications();
        List<SpecificationOption> specificationOptions = new ArrayList();
        for (Specification specification : specifications) {
            specificationOptions.addAll(specification.getSpecificationOptions());
        }
        for (SpecificationOption specificationOption : specificationOptions) {
            goodsMapper.addGoodsSpecificationOption(goodsId,specificationOption.getId());
        }
        // 查询出商品详情将数据同步到es中
        GoodsDesc desc = findDesc(goodsId);
//        searchService.syncGoodsToEs(desc);
        rabbitTemplate.convertAndSend("goods_exchange","sync_goods",desc);

//        // 将数据同步到Redis中
//        CartGoods cartGoods = new CartGoods();
//        cartGoods.setGoodId(goodsId);
//        cartGoods.setGoodsName(goods.getGoodsName());
//        cartGoods.setHeaderPic(cartGoods.getHeaderPic());
//        cartGoods.setPrice(cartGoods.getPrice());
//        rabbitTemplate.convertAndSend("goods_exchane","sync_cart",cartGoods);
    }

    @Override
    public void update(Goods goods) {
        // 删除旧图片数据
        Long goodsId = goods.getId();
        QueryWrapper<GoodsImage> queryWrapper = new QueryWrapper();
        queryWrapper.eq("goodsId",goodsId);
        goodsImageMapper.delete(queryWrapper);
        // 删除旧规格项数据
        goodsMapper.deleteGoodsSpecificationOption(goodsId);
        // 新增商品数据
        goodsMapper.updateById(goods);
        // 新增商品图片数据
        List<GoodsImage> images = goods.getImages();
        for (GoodsImage image : images) {
            image.setGoodsId(goodsId);
            goodsImageMapper.insert(image);
        }
        // 新增商品规格项数据
        List<Specification> specifications = goods.getSpecifications();
        List<SpecificationOption> specificationOptions = new ArrayList();
        for (Specification specification : specifications) {
            specificationOptions.addAll(specification.getSpecificationOptions());
        }
        for (SpecificationOption specificationOption : specificationOptions) {
            goodsMapper.addGoodsSpecificationOption(goodsId,specificationOption.getId());
        }
        GoodsDesc desc = findDesc(goodsId);
//        searchService.syncGoodsToEs(desc);
        rabbitTemplate.convertAndSend("goods_exchange","sync_goods",desc);

        // 将数据同步到Redis中
        CartGoods cartGoods = new CartGoods();
        cartGoods.setGoodId(goods.getId());
        cartGoods.setGoodsName(goods.getGoodsName());
        cartGoods.setHeaderPic(goods.getHeaderPic());
        cartGoods.setPrice(goods.getPrice());
        rabbitTemplate.convertAndSend("goods_exchange","sync_cart",cartGoods);
    }

    @Override
    public Goods findById(Long id) {
        return goodsMapper.findById(id);
    }

    @Override
    public void putAway(Long id, Boolean isMarketable) {
        goodsMapper.putAway(id,isMarketable);
        // 上架时同步到es 下架时删除es中的的数据
        if(isMarketable){
            GoodsDesc goodsDesc = findDesc(id);
//            searchService.syncGoodsToEs(goodsDesc);
            rabbitTemplate.convertAndSend("goods_exchange","sync_goods",goodsDesc);
        }else{
//            searchService.delete(id);
            rabbitTemplate.convertAndSend("goods_exchange","del_goods",id);
            // 将商品数据同步到Redis中
            CartGoods cartGoods = new CartGoods();
            cartGoods.setGoodId(id);
            rabbitTemplate.convertAndSend("goods_exchange","del_cart",cartGoods);
        }
    }

    @Override
    public Page<Goods> search(Goods goods, int page, int size) {
        QueryWrapper<Goods> queryWrapper = new QueryWrapper();
        if(goods != null && StringUtils.hasText(goods.getGoodsName())){
            queryWrapper.like("goodsName",goods.getGoodsName());
        }
        Page selectPage = goodsMapper.selectPage(new Page(page, size), queryWrapper);
        return selectPage;
    }

    @Override
    public List<GoodsDesc> findAll() {
        return goodsMapper.findAll();
    }

    @Override
    public GoodsDesc findDesc(Long id) {
        return goodsMapper.findDesc(id);
    }
}
