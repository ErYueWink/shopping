package com.itbaizhan.shopping_seckill_customer_api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.shopping_common.pojo.Orders;
import com.itbaizhan.shopping_common.pojo.SeckillGoods;
import com.itbaizhan.shopping_common.result.BaseResult;
import com.itbaizhan.shopping_common.service.OrderService;
import com.itbaizhan.shopping_common.service.SeckillService;
import com.itbaizhan.shopping_common.util.JWTUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/seckillGoods")
public class SeckillGoodsController {

    @DubboReference
    private SeckillService seckillService;
    @DubboReference
    private OrderService orderService;

    @GetMapping("/findPage")
    public BaseResult<Page<SeckillGoods>> findPage(int page, int size){
        Page<SeckillGoods> pageByRedis = seckillService.findPageByRedis(page, size);
        return BaseResult.ok(pageByRedis);
    }

    @GetMapping("/findById")
    public BaseResult<SeckillGoods> findById(Long id){
        SeckillGoods goodsByRedis = seckillService.findSeckillGoodsByRedis(id);
        return BaseResult.ok(goodsByRedis);
    }

    @PostMapping("/add")
    public BaseResult<Orders> add(@RequestBody Orders orders,@RequestHeader String token){
        Long userId = JWTUtil.getId(token);
        orders.setUserId(userId);
        Orders order = seckillService.createOrder(orders);
        return BaseResult.ok(order);
    }

    @GetMapping("/findOrder")
    public BaseResult<Orders> findOrder(String id){
        Orders order = seckillService.findOrder(id);
        return BaseResult.ok(order);
    }

    @GetMapping("/pay")
    public BaseResult<Orders> pay(String id){
        Orders orders = seckillService.pay(id);
        // 在数据库新增订单数据
        orderService.add(orders);
        return BaseResult.ok(orders);
    }
}
