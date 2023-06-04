package com.itbaizhan.shopping_pay_service.service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.itbaizhan.shopping_common.exception.BusException;
import com.itbaizhan.shopping_common.pojo.Orders;
import com.itbaizhan.shopping_common.pojo.Payment;
import com.itbaizhan.shopping_common.result.CodeEnum;
import com.itbaizhan.shopping_common.service.ZfbPayService;
import com.itbaizhan.shopping_pay_service.ZfbPayConfig;
import com.itbaizhan.shopping_pay_service.mapper.PaymentMapper;
import com.itbaizhan.shopping_pay_service.util.ZfbVerifierUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@DubboService
public class ZfbPayServiceImpl implements ZfbPayService {

    @Autowired
    private ZfbPayConfig zfbPayConfig;
    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private PaymentMapper paymentMapper;

    @Override
    public String pcPay(Orders orders) {
        // 创建支付请求
        AlipayTradePrecreateRequest alipayTradePrecreateRequest = new AlipayTradePrecreateRequest();
        // 设置请求内容
        alipayTradePrecreateRequest.setNotifyUrl(zfbPayConfig.getNotifyUrl()+zfbPayConfig.getPcNotify());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("out_trade_no",orders.getId()); // 订单编号
        jsonObject.put("total_amount",orders.getPayment()); // 支付金额
        jsonObject.put("subject",orders.getCartGoods().get(0).getGoodsName()); // 订单标题
        alipayTradePrecreateRequest.setBizContent(jsonObject.toString());
        // 发送请求
        try {
            AlipayTradePrecreateResponse alipayTradePrecreateResponse = alipayClient.execute(alipayTradePrecreateRequest);
            // 生成二维码
            return alipayTradePrecreateResponse.getQrCode(); // 二维码字符串
        } catch (AlipayApiException e) {
            throw new BusException(CodeEnum.ZFB_PAY_ERROR);
        }

    }

    @Override
    public void checkSign(Map<String, Object> paramMap) {
        // 获取参数
        Map<String,String[]> requestParameterMap = (Map<String, String[]>) paramMap.get("requestParameterMap");
        // 验签
        boolean valid = ZfbVerifierUtils.isValid(requestParameterMap, zfbPayConfig.getPublicKey());
        if(!valid){
            throw new BusException(CodeEnum.CHECK_SIGN_ERROR);
        }
    }

    @Override
    public void addPayment(Payment payment) {
        paymentMapper.insert(payment);
    }
}
