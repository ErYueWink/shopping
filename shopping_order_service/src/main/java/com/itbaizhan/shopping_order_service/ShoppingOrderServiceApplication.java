package com.itbaizhan.shopping_order_service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.itbaizhan.shopping_order_service.mapper")
public class ShoppingOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingOrderServiceApplication.class, args);
    }

}
