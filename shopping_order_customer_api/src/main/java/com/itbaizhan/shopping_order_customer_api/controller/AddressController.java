package com.itbaizhan.shopping_order_customer_api.controller;

import com.itbaizhan.shopping_common.pojo.Address;
import com.itbaizhan.shopping_common.pojo.Area;
import com.itbaizhan.shopping_common.pojo.City;
import com.itbaizhan.shopping_common.pojo.Province;
import com.itbaizhan.shopping_common.result.BaseResult;
import com.itbaizhan.shopping_common.service.AddressService;
import com.itbaizhan.shopping_common.util.JWTUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/address")
public class AddressController {

    @DubboReference
    private AddressService addressService;

    @GetMapping("/findAllProvince")
    public BaseResult<List<Province>> findAllProvince(){
        List<Province> allProvince = addressService.findAllProvince();
        return BaseResult.ok(allProvince);
    }

    @GetMapping("/findCityByProvince")
    public BaseResult<List<City>> findCityProvince(Long provinceId){
        List<City> cityList = addressService.findCityByProvince(provinceId);
        return BaseResult.ok(cityList);
    }

    @GetMapping("/findAreaByCity")
    public BaseResult<List<Area>> findAreaByCity(Long cityId){
        List<Area> areaList = addressService.findAreaByCity(cityId);
        return BaseResult.ok(areaList);
    }

    @PostMapping("/add")
    public BaseResult add(@RequestHeader String token, @RequestBody Address address){
        // 获取用户id
        Long userId = JWTUtil.getId(token);
        address.setUserId(userId);
        addressService.add(address);
        return BaseResult.ok();
    }

    @DeleteMapping("/delete")
    public BaseResult delete(Long id){
        addressService.delete(id);
        return BaseResult.ok();
    }

    @GetMapping("/findByUser")
    public BaseResult<List<Address>> findByUser(@RequestHeader String token){
        // 获取用户id
        Long userId = JWTUtil.getId(token);
        List<Address> addressList = addressService.findByUser(userId);
        return BaseResult.ok(addressList);
    }

    @GetMapping("/findById")
    public BaseResult<Address> findById(Long id){
        Address address = addressService.findById(id);
        return BaseResult.ok(address);
    }

    @PutMapping("/update")
    public BaseResult update(@RequestHeader String token,@RequestBody Address address){
        // 获取用户id
        Long userId = JWTUtil.getId(token);
        address.setUserId(userId);
        addressService.update(address);
        return BaseResult.ok();
    }
}
