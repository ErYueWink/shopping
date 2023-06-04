package com.itbaizhan.shopping_common.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 封装统一返回结果集
 * 方便前后端交互 需要给前端返回固定的格式
 */
@Data
@AllArgsConstructor
public class BaseResult<T> implements Serializable {
    // 状态码
    private Integer code;
    // 提示信息
    private String message;
    // 返回数据
    private T data;

    // 不带有数据的返回结果
    public static <T> BaseResult<T> ok(){
        return new BaseResult(CodeEnum.SUCCESS.getCode(), CodeEnum.SUCCESS.getMessage(), null);
    }

    // 带有数据的返回结果
    public static <T> BaseResult<T> ok(T data){
        return new BaseResult(CodeEnum.SUCCESS.getCode(), CodeEnum.SUCCESS.getMessage(), data);
    }
}
