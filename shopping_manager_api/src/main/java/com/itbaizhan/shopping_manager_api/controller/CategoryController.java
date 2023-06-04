package com.itbaizhan.shopping_manager_api.controller;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.shopping_common.pojo.Category;
import com.itbaizhan.shopping_common.result.BaseResult;
import com.itbaizhan.shopping_common.service.CategoryService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @DubboReference
    private CategoryService categoryService;

    @GetMapping("/search")
    public BaseResult<Page<Category>> search(int page, int size){
        Page<Category> categoryPage = categoryService.search(page, size);
        return BaseResult.ok(categoryPage);
    }

    @GetMapping("/findById")
    public BaseResult<Category> findById(Long id){
        Category category = categoryService.findById(id);
        return BaseResult.ok(category);
    }

    @PostMapping("/add")
    public BaseResult add(@RequestBody Category category){
        categoryService.add(category);
        return BaseResult.ok();
    }

    @PutMapping("/update")
    public BaseResult update(@RequestBody Category category){
        categoryService.update(category);
        return BaseResult.ok();
    }

    @PutMapping("/updateStatus")
    public BaseResult updateStatus(Long id, Integer status){
        categoryService.updateStatus(id,status);
        return BaseResult.ok();
    }

    @DeleteMapping("/delete")
    public BaseResult delete(Long[] ids){
        categoryService.delete(ids);
        return BaseResult.ok();
    }


}
