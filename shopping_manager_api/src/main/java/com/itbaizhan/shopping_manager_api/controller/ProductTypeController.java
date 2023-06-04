package com.itbaizhan.shopping_manager_api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.shopping_common.pojo.ProductType;
import com.itbaizhan.shopping_common.result.BaseResult;
import com.itbaizhan.shopping_common.service.ProductTypeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productType")
public class ProductTypeController {

    @DubboReference
    private ProductTypeService productTypeService;

    @PostMapping("/add")
    public BaseResult add(@RequestBody ProductType productType){
        productTypeService.add(productType);
        return BaseResult.ok();
    }

    @PutMapping("/update")
    public BaseResult update(@RequestBody ProductType productType){
        productTypeService.update(productType);
        return BaseResult.ok();
    }

    @DeleteMapping("/delete")
    public BaseResult delete(Long id){
        productTypeService.delete(id);
        return BaseResult.ok();
    }

    @GetMapping("/findById")
    public BaseResult<ProductType> findById(Long id){
        ProductType productType = productTypeService.findById(id);
        return BaseResult.ok(productType);
    }

    @GetMapping("/search")
    public BaseResult<Page<ProductType>> search(ProductType productType, int page, int size){
        Page<ProductType> productTypePage = productTypeService.search(productType, page, size);
        return BaseResult.ok(productTypePage);
    }

    @GetMapping("/findProductType")
    public BaseResult<List<ProductType>> findProductType(ProductType productType){
        List<ProductType> productTypeList = productTypeService.findProductType(productType);
        return BaseResult.ok(productTypeList);
    }

    @GetMapping("/findByParentId")
    public BaseResult<List<ProductType>> findByParentId(Long parentId){
        ProductType productType = new ProductType();
        productType.setParentId(parentId);
        List<ProductType> productType1 = productTypeService.findProductType(productType);
        return BaseResult.ok(productType1);
    }
}
