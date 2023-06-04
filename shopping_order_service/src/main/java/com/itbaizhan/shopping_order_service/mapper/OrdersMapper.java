package com.itbaizhan.shopping_order_service.mapper;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itbaizhan.shopping_common.pojo.Orders;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrdersMapper extends BaseMapper<Orders> {
    // 根据id查询订单
    Orders findById(String id);
    // 根据用户id和订单状态查询用户订单数据
    List<Orders> findOrdersByUserIdAndStatus(@Param("userId") Long userId, @Param("status")Integer status);
}
