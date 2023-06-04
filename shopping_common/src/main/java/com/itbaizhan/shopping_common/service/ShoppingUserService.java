package com.itbaizhan.shopping_common.service;

import com.itbaizhan.shopping_common.pojo.ShoppingUser;

// 前台用户服务
public interface ShoppingUserService {

    // 用户注册时，将手机号和验证码保存到redis中
    void saveRegisterCheckCode(String phone,String checkCode);
    // 注册时验证注册验证码
    void registerCheckCode(String phone,String checkCode);
    // 注册
    void register(ShoppingUser shoppingUser);
    // 用户名密码登录
    String loginPassword(String username,String password);

    // 登录时将手机号和验证码保存到redis中
    void saveLoginCheckCode(String phone,String checkCode);
    // 手机号验证码登录
    String loginCheckCode(String phone,String checkCode);

    // 获取登录用户名
    String getName(String token);
    // 获取登录用户
    ShoppingUser getLoginUser(String token);
}
