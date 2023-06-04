package com.itbaizhan.shopping_common.service;

import com.itbaizhan.shopping_common.pojo.GoodsDesc;
import com.itbaizhan.shopping_common.pojo.GoodsSearchParam;
import com.itbaizhan.shopping_common.pojo.GoodsSearchResult;

import java.util.List;

// 搜索服务
public interface SearchService {

    // 自动补齐关键字
    List<String> autoSuggest(String keyword);

    // 搜索
    GoodsSearchResult search(GoodsSearchParam goodsSearchParam);

    // 向ES中同步数据
    void syncGoodsToEs(GoodsDesc goodsDesc);

    // 删除商品
    void delete(Long id);

}
