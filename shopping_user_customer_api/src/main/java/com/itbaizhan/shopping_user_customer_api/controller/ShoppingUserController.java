package com.itbaizhan.shopping_user_customer_api.controller;

import com.itbaizhan.shopping_common.pojo.ShoppingUser;
import com.itbaizhan.shopping_common.result.BaseResult;
import com.itbaizhan.shopping_common.service.MessageService;
import com.itbaizhan.shopping_common.service.ShoppingUserService;
import com.itbaizhan.shopping_common.util.RandomUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/shoppingUser")
public class ShoppingUserController {

    @DubboReference
    private MessageService messageService;
    @DubboReference
    private ShoppingUserService shoppingUserService;

    /**
     * 发送验证码
     * @param phone 手机号
     * @return
     */
    @GetMapping("/sendMessage")
    public BaseResult sendMessage(String phone){
        // 生成四位数验证码
        String checkCode = RandomUtil.buildCheckCode(4);
        // 发送短信
        BaseResult baseResult = messageService.sendMessage(phone, checkCode);
        // 发送成功，将验证码保存在redis中，发送失败，返回发送结果
        if(200 == baseResult.getCode()){
            shoppingUserService.saveRegisterCheckCode(phone,checkCode);
            return BaseResult.ok();
        }else{
            return baseResult;
        }
    }

    /**
     * 验证注册验证码
     * @param phone 手机号
     * @param checkCode 验证码
     * @return
     */
    @GetMapping("/registerCheckCode")
    public BaseResult registerCheckCode(String phone,String checkCode){
        shoppingUserService.registerCheckCode(phone,checkCode);
        return BaseResult.ok();
    }

    @PostMapping("/register")
    public BaseResult register(@RequestBody ShoppingUser shoppingUser){
        shoppingUserService.register(shoppingUser);
        return BaseResult.ok();
    }

    @PostMapping("/loginPassword")
    public BaseResult<String> login(@RequestBody ShoppingUser shoppingUser){
        String username = shoppingUserService.loginPassword(shoppingUser.getUsername(), shoppingUser.getPassword());
        return BaseResult.ok(username);
    }

    @GetMapping("/sendLoginCheckCode")
    public BaseResult sendLoginCheckCode(String phone){
        // 1.生成随机四位数
        String checkCode = RandomUtil.buildCheckCode(4);
        // 2.发送短信
        BaseResult result = messageService.sendMessage(phone, checkCode);
        // 3.发送成功，将验证码保存到redis中,发送失败，返回发送结果
        if (200 == result.getCode()) {
            shoppingUserService.saveLoginCheckCode(phone, checkCode);
            return BaseResult.ok();
        } else {
            return result;
        }
    }

    @PostMapping("/loginCheckCode")
    public BaseResult<String> loginCheckCode(String phone,String checkCode){
        String username = shoppingUserService.loginCheckCode(phone, checkCode);
        return BaseResult.ok(username);
    }

    @GetMapping("/getName")
    public BaseResult<String> getName(@RequestHeader String token){
        String username = shoppingUserService.getName(token);
        return BaseResult.ok(username);
    }

}
