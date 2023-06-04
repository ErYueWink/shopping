package com.itbaizhan.shopping_common.service;

import com.itbaizhan.shopping_common.pojo.Orders;
import com.itbaizhan.shopping_common.pojo.Payment;

import java.util.Map;

public interface ZfbPayService {

    // 生成二维码
    String pcPay(Orders orders);

    /**
     * 验签
     * @param paramMap 支付相关参数
     */
    void checkSign(Map<String,Object> paramMap);

    // 新增交易记录
    void addPayment(Payment payment);
}
