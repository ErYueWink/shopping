package com.itbaizhan.shopping_common.service;

import com.itbaizhan.shopping_common.result.BaseResult;

// 发送短信服务
public interface MessageService {

    /**
     * 发送短信
     * @param phoneNumber 手机号
     * @param code 验证码
     * @return 使用阿里云短信测试，返回的结果集就包含了状态码以及提示信息
     */
    BaseResult sendMessage(String phoneNumber,String code);
}
