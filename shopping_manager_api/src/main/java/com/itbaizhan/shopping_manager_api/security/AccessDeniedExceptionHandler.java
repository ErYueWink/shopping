package com.itbaizhan.shopping_manager_api.security;

import com.itbaizhan.shopping_common.result.BaseResult;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



@RestControllerAdvice
public class AccessDeniedExceptionHandler {
    
    // 当捕获到权限不足异常直接抛出
    @ExceptionHandler(AccessDeniedException.class)
    public BaseResult defaultExceptionHandler(AccessDeniedException e){
        throw e;
    }

    }
