package com.itbaizhan.shopping_common.exception;

import com.itbaizhan.shopping_common.result.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一异常
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusException extends RuntimeException implements Serializable {

    // 状态码
    private Integer code;
    // 提示信息
    private String message;

    public BusException(CodeEnum codeEnum){
        this.code = codeEnum.getCode();
        this.message = codeEnum.getMessage();
    }
}
