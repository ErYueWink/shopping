package com.itbaizhan.shopping_common.exception;

import com.itbaizhan.shopping_common.result.BaseResult;
import com.itbaizhan.shopping_common.result.CodeEnum;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // 业务异常
    @ExceptionHandler(BusException.class)
    public BaseResult defaultExceptionHandler(HttpServletRequest request, HttpServletResponse response,BusException e){
        BaseResult baseResult = new BaseResult(e.getCode(),e.getMessage(),null);
        return baseResult;
    }
    // 系统异常
    @ExceptionHandler(Exception.class)
    public BaseResult defaultExceptionHandler(HttpServletRequest request, HttpServletResponse response,Exception e) {
        e.printStackTrace();
        BaseResult baseResult = new BaseResult(CodeEnum.SYSTEM_ERROR.getCode(),CodeEnum.SYSTEM_ERROR.getMessage(),null);
        return baseResult;

    }
    
    }
