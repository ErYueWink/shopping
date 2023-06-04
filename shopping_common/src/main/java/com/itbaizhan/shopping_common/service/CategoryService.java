package com.itbaizhan.shopping_common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.shopping_common.pojo.Category;

import java.util.List;

// 广告服务
public interface CategoryService {

    // 新增广告
    void add(Category category);
    // 修改广告
    void update(Category category);
    // 修改广告状态
    void updateStatus(Long id,Integer status);
    // 批量删除广告
    void delete(Long[] ids);
    // 根据id查询广告
    Category findById(Long id);
    // 查询所有广告
    List<Category> findAll();
    // 分页查询广告
    Page<Category> search(int page,int size);
}
