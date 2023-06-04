package com.itbaizhan.shopping_search_customer_api.controller;

import com.itbaizhan.shopping_common.pojo.GoodsDesc;
import com.itbaizhan.shopping_common.pojo.GoodsSearchParam;
import com.itbaizhan.shopping_common.pojo.GoodsSearchResult;
import com.itbaizhan.shopping_common.result.BaseResult;
import com.itbaizhan.shopping_common.service.GoodsService;
import com.itbaizhan.shopping_common.service.SearchService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/goodsSearch")
public class GoodsSearchController {

    @DubboReference
    private SearchService searchService;
    @DubboReference
    private GoodsService goodsService;

    @GetMapping("/autoSuggest")
    public BaseResult<List<String>> autoSuggest(String keyword){
        List<String> suggest = searchService.autoSuggest(keyword);
        return BaseResult.ok(suggest);
    }

    @PostMapping("/search")
    public BaseResult<GoodsSearchResult> search(@RequestBody GoodsSearchParam goodsSearchParam){
        GoodsSearchResult search = searchService.search(goodsSearchParam);
        return BaseResult.ok(search);
    }

    @GetMapping("/findDesc")
    public BaseResult<GoodsDesc> findDesc(Long id){
        GoodsDesc goodsDesc = goodsService.findDesc(id);
        return BaseResult.ok(goodsDesc);
    }
}
