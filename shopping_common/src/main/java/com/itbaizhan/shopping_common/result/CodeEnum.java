package com.itbaizhan.shopping_common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CodeEnum {

    SUCCESS(200,"操作成功"),
    SYSTEM_ERROR(500,"系统异常"),
    PARAMETER_ERROR(601,"参数异常"),
    PRODUCT_TYPE_ERROR(602,"该商品类型不能子类型"),
    DELETE_PRODUCT_ERROR(603,"该商品有子类型不能删除"),
    UPLOAD_FILE_ERROR(604,"图片上传失败"),
    REGISTER_CODE_ERROR(605,"注册验证码错误"),
    REGISTER_REPEAT_PHONE_ERORR(606,"手机号重复"),
    REGISTER_REPEAT_USERNAME_ERORR(607,"用户名重复"),
    LOGIN_NAME_PASSWORD_ERROR(608,"用户名或密码错误"),
    LOGIN_CHECK_CODE_ERROR(609,"登录验证码错误"),
    VERIFY_TOKEN_ERROR(611,"令牌解析错误"),
    ZFB_PAY_ERROR(612,"支付宝支付错误"),
    CHECK_SIGN_ERROR(613,"支付宝验签错误"),
    STOCK_COUNT_ERROR(614,"库存不足"),
    ORDER_PAY_ERROR(615,"订单已超时");

    private final Integer code; // 状态码
    private final String message; // 提示信息
}
