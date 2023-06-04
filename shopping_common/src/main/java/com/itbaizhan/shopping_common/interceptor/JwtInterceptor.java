package com.itbaizhan.shopping_common.interceptor;

import com.itbaizhan.shopping_common.util.JWTUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取与请求头中令牌数据
        String token = request.getHeader("token");
        // 解析令牌
        JWTUtil.verify(token);
        return true;
    }
}
