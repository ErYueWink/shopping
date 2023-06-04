package com.itbaizhan.shopping_category_service.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.shopping_category_service.mapper.CategoryMapper;
import com.itbaizhan.shopping_common.pojo.Category;
import com.itbaizhan.shopping_common.service.CategoryService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;

@DubboService
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public void add(Category category) {
        categoryMapper.insert(category);
        refreshRedisCategory();
    }

    @Override
    public void update(Category category) {
        categoryMapper.updateById(category);
        refreshRedisCategory();
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Category category = categoryMapper.selectById(id);
        category.setStatus(status);
        categoryMapper.updateById(category);
    }

    @Override
    public void delete(Long[] ids) {
        categoryMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    public Category findById(Long id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public List<Category> findAll() {
        ListOperations<String,Category> listOperations = redisTemplate.opsForList();
        List<Category> categories = listOperations.range("categories", 0, -1);
        if(categories !=null && categories.size() > 0){
            System.out.println("从redis中查询");
            return categories;
        }else{
            System.out.println("从数据库中查询");
            QueryWrapper<Category> queryWrapper = new QueryWrapper();
            queryWrapper.eq("status",1);
            List<Category> categoryList = categoryMapper.selectList(queryWrapper);
            listOperations.leftPushAll("categories",categoryList);
            return categoryList;
        }
    }

    @Override
    public Page<Category> search(int page, int size) {
        return categoryMapper.selectPage(new Page(page,size),null);
    }

    // 跟新redis中的数据
    public void refreshRedisCategory(){
        // 查询全部已启用的广告
        QueryWrapper<Category> queryWrapper = new QueryWrapper();
        queryWrapper.eq("status",1);
        List<Category> categoryList = categoryMapper.selectList(queryWrapper);

        // 删除旧数据
        redisTemplate.delete("categories");
        // 向redis中重新添加数据
        ListOperations<String,Category> listOperations = redisTemplate.opsForList();
        listOperations.leftPushAll("categories",categoryList);
    }
}
