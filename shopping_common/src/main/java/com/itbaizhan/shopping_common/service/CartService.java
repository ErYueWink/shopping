package com.itbaizhan.shopping_common.service;

import com.itbaizhan.shopping_common.pojo.CartGoods;

import java.util.List;

public interface CartService {
    // 新增商品到购物车
    void addCart(Long userId, CartGoods cartGoods);
    // 修改购物车中商品数量
    void handleCart(Long userId,Long goodsId,Integer num);
    // 删除购物车中商品
    void deleteCartOption(Long userId,Long goodsId);
    // 查询购物车中的商品
    List<CartGoods> findCartList(Long userId);
    // 管理员更新购物车数据后将数据同步到redis中
    void refreshCartGoods(CartGoods cartGoods);
    // 管理员删除商品时将数据同步到redis中
    void deleteCartGoods(CartGoods cartGoods);
}
