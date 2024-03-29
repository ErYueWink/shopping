package com.itbaizhan.shopping_common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.shopping_common.pojo.Goods;
import com.itbaizhan.shopping_common.pojo.GoodsDesc;

import java.util.List;

// 商品服务
public interface GoodsService {
    // 新增商品
    void add(Goods goods);
    // 修改商品
    void update(Goods goods);
    // 根据id查询商品
    Goods findById(Long id);
    // 上下架商品
    void putAway(Long id,Boolean isMarketable);
    // 分页查询商品
    Page<Goods> search(Goods goods,int page,int size);
    // 查询所有商品详情
    List<GoodsDesc> findAll();
    // 根据id查询商品详情
    GoodsDesc findDesc(Long id);
}
