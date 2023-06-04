package com.itbaizhan.shopping_cart_customer_api.controller;

import com.itbaizhan.shopping_common.pojo.CartGoods;
import com.itbaizhan.shopping_common.result.BaseResult;
import com.itbaizhan.shopping_common.service.CartService;
import com.itbaizhan.shopping_common.util.JWTUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/cart")
public class CartGoodsController {

    @DubboReference
    private CartService cartService;

    @GetMapping("/findCartList")
    public BaseResult<List<CartGoods>> findCartList(@RequestHeader String token){
        Long userId = JWTUtil.getId(token);
        List<CartGoods> cartList = cartService.findCartList(userId);
        return BaseResult.ok(cartList);
    }

    @PostMapping("/addCart")
    public BaseResult addCart(@RequestBody CartGoods cartGoods,@RequestHeader String token){
        Long userId = JWTUtil.getId(token); // 获取用户id
        cartService.addCart(userId,cartGoods);
        return BaseResult.ok();
    }

    @GetMapping("/handleCart")
    public BaseResult handleCart(@RequestHeader String token,Long goodId,Integer num){
        Long userId = JWTUtil.getId(token);
        cartService.handleCart(userId,goodId,num);
        return BaseResult.ok();
    }


    @DeleteMapping("/deleteCart")
    public BaseResult addCart(@RequestHeader String token,Long goodId){
        Long userId = JWTUtil.getId(token); // 获取用户id
        cartService.deleteCartOption(userId,goodId);
        return BaseResult.ok();
    }
}




