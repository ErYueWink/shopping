package com.itbaizhan.shopping_common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.shopping_common.pojo.SeckillGoods;

// 秒杀商品服务
public interface SeckillGoodsService {

    // 新增秒杀商品
    void add(SeckillGoods seckillGoods);
    // 修改秒杀商品
    void update(SeckillGoods seckillGoods);
    // 分页查询秒杀商品
    Page<SeckillGoods> findPage(int page,int size);
}
