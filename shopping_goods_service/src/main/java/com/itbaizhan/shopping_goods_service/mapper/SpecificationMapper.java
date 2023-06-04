package com.itbaizhan.shopping_goods_service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itbaizhan.shopping_common.pojo.Specification;

import java.util.List;

public interface SpecificationMapper extends BaseMapper<Specification> {

    // 根据id查询商品规格以及对应的规格项
    Specification findById(Long id);
    // 查询某种商品类型下的所有规格
    List<Specification> findByProductTypeId(Long id);
}
