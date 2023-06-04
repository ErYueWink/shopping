package com.itbaizhan.shopping_user_service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.itbaizhan.shopping_user_service.mapper")
public class ShoppingUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingUserServiceApplication.class, args);
    }

}
