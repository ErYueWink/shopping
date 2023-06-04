package com.itbaizhan.shopping_user_service.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itbaizhan.shopping_common.exception.BusException;
import com.itbaizhan.shopping_common.pojo.ShoppingUser;
import com.itbaizhan.shopping_common.result.CodeEnum;
import com.itbaizhan.shopping_common.service.ShoppingUserService;
import com.itbaizhan.shopping_common.util.JWTUtil;
import com.itbaizhan.shopping_common.util.Md5Util;
import com.itbaizhan.shopping_user_service.mapper.ShoppingUserMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.concurrent.TimeUnit;

@DubboService
public class ShoppingUserServiceImpl implements ShoppingUserService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ShoppingUserMapper shoppingUserMapper;

    /**
     * 注册时将手机号和验证码保存到redis中
     * @param phone 手机号
     * @param checkCode 验证码
     */
    @Override
    public void saveRegisterCheckCode(String phone, String checkCode) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("registerCode:"+phone,checkCode,300, TimeUnit.SECONDS);
    }

    @Override
    public void registerCheckCode(String phone, String checkCode) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String code = (String) valueOperations.get("registerCode:"+phone);
        if(!checkCode.equals(code)){
            throw new BusException(CodeEnum.REGISTER_CODE_ERROR);
        }
    }

    @Override
    public void register(ShoppingUser shoppingUser) {
        // 验证手机号是否重复
        String phone = shoppingUser.getPhone();
        QueryWrapper<ShoppingUser> queryWrapper = new QueryWrapper();
        queryWrapper.eq("phone",phone);
        List<ShoppingUser> shoppingUsers = shoppingUserMapper.selectList(queryWrapper);
        if(shoppingUsers!=null && shoppingUsers.size() > 0){
            throw new BusException(CodeEnum.REGISTER_REPEAT_PHONE_ERORR);
        }
        // 验证用户名是否重复
        String name = shoppingUser.getName();
        QueryWrapper<ShoppingUser> queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("username",name);
        List<ShoppingUser> users = shoppingUserMapper.selectList(queryWrapper1);
        if(users != null && users.size() > 0){
            throw new BusException(CodeEnum.REGISTER_REPEAT_USERNAME_ERORR);
        }
        // 设置用户状态为Y
        shoppingUser.setStatus("Y");
        // 对密码加密
        shoppingUser.setPassword(Md5Util.encode(shoppingUser.getPassword()));
        // 新增用户
        shoppingUserMapper.insert(shoppingUser);
    }

    @Override
    public String loginPassword(String username, String password) {
        QueryWrapper<ShoppingUser> queryWrapper = new QueryWrapper();
        queryWrapper.eq("username",username);
        ShoppingUser shoppingUser = shoppingUserMapper.selectOne(queryWrapper);
        if(shoppingUser == null){
            throw new BusException(CodeEnum.LOGIN_NAME_PASSWORD_ERROR);
        }
        boolean verify = Md5Util.verify(password, shoppingUser.getPassword());
        if(!verify){
            throw new BusException(CodeEnum.LOGIN_NAME_PASSWORD_ERROR);
        }
        return JWTUtil.sign(shoppingUser);
    }

    // 登录时将手机号和验证码保存到redis中
    @Override
    public void saveLoginCheckCode(String phone, String checkCode) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("loginCode:"+phone,checkCode,300, TimeUnit.SECONDS);
    }
    // 验证手机号和验证码
    @Override
    public String loginCheckCode(String phone, String checkCode) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String code = (String) valueOperations.get("loginCode:" + phone);
        if(!checkCode.equals(code)){
            throw new BusException(CodeEnum.LOGIN_CHECK_CODE_ERROR);
        }
        QueryWrapper<ShoppingUser> queryWrapper = new QueryWrapper();
        queryWrapper.eq("phone",phone);
        ShoppingUser shoppingUser = shoppingUserMapper.selectOne(queryWrapper);
        return JWTUtil.sign(shoppingUser);
    }

    @Override
    public String getName(String token) {
        // 解析令牌获取用户名
        String username = JWTUtil.verify(token);
        return username;
    }

    @Override
    public ShoppingUser getLoginUser(String token) {
        String username = JWTUtil.verify(token);
        QueryWrapper<ShoppingUser> queryWrapper = new QueryWrapper();
        queryWrapper.eq("username",username);
        ShoppingUser shoppingUser = shoppingUserMapper.selectOne(queryWrapper);
        return shoppingUser;
    }
}
