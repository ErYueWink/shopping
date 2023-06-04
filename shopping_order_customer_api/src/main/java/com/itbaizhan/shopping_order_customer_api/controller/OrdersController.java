package com.itbaizhan.shopping_order_customer_api.controller;

import com.itbaizhan.shopping_common.pojo.CartGoods;
import com.itbaizhan.shopping_common.pojo.Orders;
import com.itbaizhan.shopping_common.result.BaseResult;
import com.itbaizhan.shopping_common.service.CartService;
import com.itbaizhan.shopping_common.service.OrderService;
import com.itbaizhan.shopping_common.util.JWTUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/orders")
public class OrdersController {

    @DubboReference
    private OrderService orderService;
    @DubboReference
    private CartService cartService;

    @PostMapping("/add")
    public BaseResult<Orders> add(@RequestBody Orders orders, @RequestHeader String token){
        Long userId = JWTUtil.getId(token);
        orders.setUserId(userId);
        // 保存订单数据
        orderService.add(orders);
        // 删除redis中购物车商品数据
        List<CartGoods> cartGoods = orders.getCartGoods();
        for (CartGoods cartGood : cartGoods) {
            cartService.deleteCartOption(userId, cartGood.getGoodId());
        }
        return BaseResult.ok(orders);
    }

    @GetMapping("/findById")
    public BaseResult<Orders> findById(String id){
        Orders orders = orderService.findById(id);
        return BaseResult.ok(orders);
    }

    @GetMapping("/findUserOrders")
    public BaseResult<List<Orders>> findUserOrders(@RequestHeader String token,Integer status){
        // 获取用户id
        Long userId = JWTUtil.getId(token);
        List<Orders> userOrders = orderService.findUserOrders(userId, status);
        return BaseResult.ok(userOrders);
    }

}
