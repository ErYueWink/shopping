package com.itbaizhan.shopping_order_customer_api.controller;

import com.alibaba.fastjson.JSON;
import com.itbaizhan.shopping_common.pojo.Orders;
import com.itbaizhan.shopping_common.pojo.Payment;
import com.itbaizhan.shopping_common.result.BaseResult;
import com.itbaizhan.shopping_common.service.OrderService;
import com.itbaizhan.shopping_common.service.ZfbPayService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.lucene.queryparser.xml.builders.SpanPositionRangeBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.json.Json;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/payment")
public class ZfbPayController {

    @DubboReference
    private ZfbPayService zfbPayService;
    @DubboReference
    private OrderService orderService;

    @PostMapping("/pcPay")
    public BaseResult<String> pcPay(String orderId){
        Orders orders = orderService.findById(orderId);
        String pay = zfbPayService.pcPay(orders);
        return BaseResult.ok(pay);
    }
    @PostMapping("/success/notify")
    public BaseResult successNotify(HttpServletRequest request){
        // 验签
        Map<String,Object> map = new HashMap();
        map.put("requestParameterMap",request.getParameterMap());
        zfbPayService.checkSign(map);

        String trade_status = request.getParameter("trade_status"); // 订单状态
        String out_trade_no = request.getParameter("out_trade_no"); // 订单编号

        // 付款成功
        if(trade_status.equals("TRADE_SUCCESS")){
            // 修改订单状态
            Orders orders = orderService.findById(out_trade_no);
            orders.setPaymentType(2); // 支付方式
            orders.setStatus(2); // 订单状态
            orders.setPaymentTime(new Date()); // 付款时间
            orderService.update(orders);
            // 新增交易记录
            Payment payment = new Payment();
            payment.setOrderId(out_trade_no);
            payment.setTransactionId(out_trade_no);
            payment.setTradeType("支付宝支付");
            payment.setTradeState(trade_status);
            payment.setContent(JSON.toJSONString(request.getParameterMap()));
            payment.setCreateTime(new Date());
            payment.setPayerTotal(orders.getPayment());
            zfbPayService.addPayment(payment);
        }
        return BaseResult.ok();
    }

}
