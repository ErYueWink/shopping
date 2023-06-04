package com.itbaizhan.shopping_common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.shopping_common.pojo.Orders;
import com.itbaizhan.shopping_common.pojo.SeckillGoods;

// 秒杀商品服务
public interface SeckillService {

    // 在redis中查询所有秒杀订单
    Page<SeckillGoods> findPageByRedis(int page, int size);
    // 根据商品id查询秒杀订单
    SeckillGoods findSeckillGoodsByRedis(Long goodsId);
    // 生成秒杀订单
    Orders createOrder(Orders orders);
    // 根据id查询秒杀订单
    Orders findOrder(String id);
    // 支付秒杀订单
    Orders pay(String orderId);

}
