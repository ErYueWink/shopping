package com.itbaizhan.shopping_order_service.service;

import com.itbaizhan.shopping_common.pojo.CartGoods;
import com.itbaizhan.shopping_common.pojo.Orders;
import com.itbaizhan.shopping_common.service.CartService;
import com.itbaizhan.shopping_common.service.OrderService;
import com.itbaizhan.shopping_order_service.mapper.CartGoodsMapper;
import com.itbaizhan.shopping_order_service.mapper.OrdersMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.objenesis.SpringObjenesis;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@DubboService
public class OrdersServiceImpl implements OrderService {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private CartGoodsMapper cartGoodsMapper;

    @Override
    public Orders add(Orders orders) {
        // 设置订单状态未付款
        if(orders.getStatus() == null){
            orders.setStatus(1);
        }
        // 设置订单创建时间
        orders.setCreateTime(new Date());
        // 获取订单商品(购物车)数据
        List<CartGoods> cartGoods = orders.getCartGoods();
        // 计算订单价格
        BigDecimal sum = BigDecimal.ZERO;
        for (CartGoods cartGood : cartGoods) {
            BigDecimal num = new BigDecimal(cartGood.getNum()); // 数量
            BigDecimal price = cartGood.getPrice(); // 单价
            BigDecimal count = num.multiply(price); // 总价
            sum = sum.add(count);
        }
        // 设置应付金额
        orders.setPayment(sum);
        // 保存订单数据
        ordersMapper.insert(orders);
        // 保存订单商品(购物车)数据
        for (CartGoods cartGood : cartGoods) {
            cartGood.setOrderId(orders.getId());
            cartGoodsMapper.insert(cartGood);
        }
        return orders;
    }

    // 该方法并不暴露接口，不会被用户主动调用，而是当订单支付后调用该方法，修改订单状态为已付款
    @Override
    public void update(Orders orders) {
        ordersMapper.updateById(orders);
    }

    @Override
    public Orders findById(String id) {
        return ordersMapper.findById(id);
    }

    @Override
    public List<Orders> findUserOrders(Long userId, Integer status) {
        return ordersMapper.findOrdersByUserIdAndStatus(userId,status);
    }
}
