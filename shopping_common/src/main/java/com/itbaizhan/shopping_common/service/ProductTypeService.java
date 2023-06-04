package com.itbaizhan.shopping_common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.shopping_common.pojo.ProductType;

import java.util.List;

// 商品类型服务
public interface ProductTypeService {

    // 新增商品
    void add(ProductType productType);
    // 修改商品
    void update(ProductType productType);
    // 删除商品
    void delete(Long id);
    // 根据id查询商品
    ProductType findById(Long id);
    // 分页查询商品
    Page<ProductType> search(ProductType productType,int page,int size);
    // 根据条件查询商品
    List<ProductType> findProductType(ProductType productType);
}
