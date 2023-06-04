//package com.itbaizhan.shopping_user_customer_api;
//
//import com.itbaizhan.shopping_common.interceptor.JwtInterceptor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class InterceptorConfig implements WebMvcConfigurer {
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new JwtInterceptor())
//                .addPathPatterns("/**") // 拦截的接口
//                .excludePathPatterns(
//                        "/user/shoppingUser/sendMessage",
//                        "/user/shoppingUser/registerCheckCode",
//                        "/user/shoppingUser/register",
//                        "/user/shoppingUser/loginPassword",
//                        "/user/shoppingUser/sendLoginCheckCode",
//                        "/user/shoppingUser/loginCheckCode"
//                ); // 放行的接口
//    }
//}
