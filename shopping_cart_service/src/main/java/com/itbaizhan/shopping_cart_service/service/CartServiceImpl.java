package com.itbaizhan.shopping_cart_service.service;

import com.itbaizhan.shopping_common.pojo.CartGoods;
import com.itbaizhan.shopping_common.service.CartService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@DubboService
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @RabbitListener(queues = "sync_cart_queue")
    public void listenerSyncQueue(CartGoods cartGoods){
        refreshCartGoods(cartGoods);
    }
    @RabbitListener(queues = "del_cart_queue")
    public void listenerDelQueue(CartGoods cartGoods){
        deleteCartGoods(cartGoods);
    }
    @Override
    public void addCart(Long userId, CartGoods cartGoods) {
        List<CartGoods> cartList = findCartList(userId);
        for (CartGoods goods : cartList) {
            if(goods.getGoodId().equals(cartGoods.getGoodId())){
                Integer num = goods.getNum() + cartGoods.getNum();
                goods.setNum(num);
                redisTemplate.boundHashOps("cartList").put(userId,cartList);
                return;
            }
        }
        cartList.add(cartGoods);
        redisTemplate.boundHashOps("cartList").put(userId,cartList);
    }

    @Override
    public void handleCart(Long userId, Long goodsId, Integer num) {
        List<CartGoods> cartList = findCartList(userId);
        // 找到对应商品
        for (CartGoods cartGoods : cartList) {
            if(goodsId.equals(cartGoods.getGoodId())){
                cartGoods.setNum(num);
                break;
            }
        }
        redisTemplate.boundHashOps("cartList").put(userId,cartList);
    }

    @Override
    public void deleteCartOption(Long userId, Long goodsId) {
        List<CartGoods> cartList = findCartList(userId);
        // 找到对应商品
        for (CartGoods cartGoods : cartList) {
            if(goodsId.equals(cartGoods.getGoodId())){
               cartList.remove(cartGoods);
                break;
            }
        }
        redisTemplate.boundHashOps("cartList").put(userId,cartList);
    }

    @Override
    public List<CartGoods> findCartList(Long userId) {
        // 从redis中查询用户购物车，如果有，返回，如果没有则返回一个空集合
        BoundHashOperations boundHashOps = redisTemplate.boundHashOps("cartList");
        Object cart = boundHashOps.get(userId);
        if(cart == null){
            return new ArrayList();
        }else{
            return (List<CartGoods>) cart;
        }
    }

    // 同步购物车中的商品数据
    @Override
    public void refreshCartGoods(CartGoods cartGoods) {
        // 获取所有购物车数据
        BoundHashOperations cartList = redisTemplate.boundHashOps("cartList");
        Map<Long,List<CartGoods>> entries = cartList.entries();
        Set<Map.Entry<Long, List<CartGoods>>> entrySet = entries.entrySet();
        for (Map.Entry<Long, List<CartGoods>> entry : entrySet) {
            // 一个用户购物车
            List<CartGoods> goodsList = entry.getValue();
            for (CartGoods goods : goodsList) {
                if(goods.getGoodId().equals(cartGoods.getGoodId())){
                    goods.setGoodsName(cartGoods.getGoodsName());
                    goods.setHeaderPic(cartGoods.getHeaderPic());
                    goods.setPrice(cartGoods.getPrice());
                    break;
                }
            }
        }

        redisTemplate.delete("cartList");
        redisTemplate.boundHashOps("cartList").putAll(entries);
    }

    @Override
    public void deleteCartGoods(CartGoods cartGoods) {
// 获取所有购物车数据
        BoundHashOperations cartList = redisTemplate.boundHashOps("cartList");
        Map<Long,List<CartGoods>> entries = cartList.entries();
        Set<Map.Entry<Long, List<CartGoods>>> entrySet = entries.entrySet();
        for (Map.Entry<Long, List<CartGoods>> entry : entrySet) {
            // 一个用户购物车
            List<CartGoods> goodsList = entry.getValue();
            for (CartGoods goods : goodsList) {
                if(goods.getGoodId().equals(cartGoods.getGoodId())){
                    goodsList.remove(goods);
                    break;
                }
            }
        }

        redisTemplate.delete("cartList");
        redisTemplate.boundHashOps("cartList").putAll(entries);
    }
}
