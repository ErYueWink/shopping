package com.itbaizhan.shopping_goods_service.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.shopping_common.exception.BusException;
import com.itbaizhan.shopping_common.pojo.ProductType;
import com.itbaizhan.shopping_common.result.CodeEnum;
import com.itbaizhan.shopping_common.service.ProductTypeService;
import com.itbaizhan.shopping_goods_service.mapper.ProductTypeMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@DubboService
@Transactional
public class ProductTypeServiceImpl implements ProductTypeService {

    @Autowired
    private ProductTypeMapper productTypeMapper;

    @Override
    public void add(ProductType productType) {
        // 前端会传来父类型的id，商品类型级别需要我们自己指定
        Long parentId = productType.getParentId(); // 父类型id
        ProductType productTypeParent = productTypeMapper.selectById(parentId);
        // 当父类型id==0时并不会在数据库查询出数据
        if(productTypeParent == null){
            productType.setLevel(1);
        }else if(productTypeParent.getLevel() < 3){
            productType.setLevel(productTypeParent.getLevel() + 1);
        }else if(productTypeParent.getLevel() >= 3){
            throw new BusException(CodeEnum.PRODUCT_TYPE_ERROR);
        }
        productTypeMapper.insert(productType);
    }

    @Override
    public void update(ProductType productType) {
// 前端会传来父类型的id，商品类型级别需要我们自己指定
        Long parentId = productType.getParentId(); // 父类型id
        ProductType productTypeParent = productTypeMapper.selectById(parentId);
        // 当父类型id==0时并不会在数据库查询出数据
        if(productTypeParent == null){
            productType.setLevel(1);
        }else if(productTypeParent.getLevel() < 3){
            productType.setLevel(productTypeParent.getLevel() + 1);
        }else if(productTypeParent.getLevel() >= 3){
            throw new BusException(CodeEnum.PRODUCT_TYPE_ERROR);
        }
        productTypeMapper.updateById(productType);
    }

    @Override
    public void delete(Long id) {
        // 如果该商品有子类型则删除失败
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("parentId",id);
        List<ProductType> productTypeList = productTypeMapper.selectList(queryWrapper);
        // 该商品有子类型不能删除
        if(productTypeList != null && productTypeList.size()>0){
            throw new BusException(CodeEnum.DELETE_PRODUCT_ERROR);
        }
        productTypeMapper.deleteById(id);
    }

    @Override
    public ProductType findById(Long id) {
        return productTypeMapper.selectById(id);
    }

    @Override
    public Page<ProductType> search(ProductType productType, int page, int size) {
        QueryWrapper<ProductType> queryWrapper = new QueryWrapper();
        if(productType != null){
            // 如果产品名不为空则根据产品名进行模糊查询
            if(StringUtils.hasText(productType.getName())){
                queryWrapper.like("name",productType.getName());
            }
            // 上级类别id不为空
            if(productType.getParentId() != null){
                queryWrapper.eq("parentId",productType.getParentId());
            }
        }
        Page selectPage = productTypeMapper.selectPage(new Page(page, size), queryWrapper);
        return selectPage;
    }

    @Override
    public List<ProductType> findProductType(ProductType productType) {
        QueryWrapper<ProductType> queryWrapper = new QueryWrapper();
        if(productType != null){
            // 根据产品名进行模糊查询
            if(StringUtils.hasText(productType.getName())){
                queryWrapper.like("name",productType.getName());
            }
            // 根据上级id查询
            if(productType.getParentId() != null){
                queryWrapper.eq("parentId",productType.getParentId());
            }
        }
        List<ProductType> productTypeList = productTypeMapper.selectList(queryWrapper);
        return productTypeList;
    }
}
