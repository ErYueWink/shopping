package com.itbaizhan.shopping_order_service.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itbaizhan.shopping_common.pojo.Address;
import com.itbaizhan.shopping_common.pojo.Area;
import com.itbaizhan.shopping_common.pojo.City;
import com.itbaizhan.shopping_common.pojo.Province;
import com.itbaizhan.shopping_common.service.AddressService;
import com.itbaizhan.shopping_order_service.mapper.AddressMapper;
import com.itbaizhan.shopping_order_service.mapper.AreaMapper;
import com.itbaizhan.shopping_order_service.mapper.CityMapper;
import com.itbaizhan.shopping_order_service.mapper.ProvinceMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@DubboService
@Transactional
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private ProvinceMapper provinceMapper;
    @Autowired
    private CityMapper cityMapper;
    @Autowired
    private AreaMapper areaMapper;

    @Override
    public List<Province> findAllProvince() {
        List<Province> provinces = provinceMapper.selectList(null);
        return provinces;
    }

    @Override
    public List<City> findCityByProvince(Long provinceId) {
        QueryWrapper<City> queryWrapper = new QueryWrapper();
        queryWrapper.eq("provinceId",provinceId);
        List<City> cities = cityMapper.selectList(queryWrapper);
        return cities;
    }

    @Override
    public List<Area> findAreaByCity(Long cityId) {
        QueryWrapper<Area> queryWrapper = new QueryWrapper();
        queryWrapper.eq("cityId",cityId);
        List<Area> areaList = areaMapper.selectList(queryWrapper);
        return areaList;
    }

    @Override
    public void add(Address address) {
        addressMapper.insert(address);
    }

    @Override
    public void update(Address address) {
        addressMapper.updateById(address);
    }

    @Override
    public Address findById(Long id) {
        Address address = addressMapper.selectById(id);
        return address;
    }

    @Override
    public void delete(Long id) {
        addressMapper.deleteById(id);
    }

    @Override
    public List<Address> findByUser(Long userId) {
        QueryWrapper<Address> queryWrapper = new QueryWrapper();
        queryWrapper.eq("userId",userId);
        List<Address> addresses = addressMapper.selectList(queryWrapper);
        return addresses;
    }
}
