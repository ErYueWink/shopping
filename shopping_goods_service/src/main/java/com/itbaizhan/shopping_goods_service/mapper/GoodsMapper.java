package com.itbaizhan.shopping_goods_service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itbaizhan.shopping_common.pojo.Goods;
import com.itbaizhan.shopping_common.pojo.GoodsDesc;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsMapper extends BaseMapper<Goods> {
    // 新增商品规格项数据
    void addGoodsSpecificationOption(@Param("gid") Long gid, @Param("optionId") Long optionId);
    // 删除商品规格项数据
    void deleteGoodsSpecificationOption(Long gid);
    // 上下架商品
    void putAway(@Param("id") Long id,@Param("isMarketable") Boolean isMarketable);
    //  根据id查询商品详情
    Goods findById(Long id);

    // 查询所有商品详情
    List<GoodsDesc> findAll();

    // 根据id查询商品详情
    GoodsDesc findDesc(Long id);
}
