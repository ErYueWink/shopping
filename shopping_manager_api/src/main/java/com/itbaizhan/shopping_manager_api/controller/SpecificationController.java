package com.itbaizhan.shopping_manager_api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.shopping_common.pojo.Specification;
import com.itbaizhan.shopping_common.pojo.SpecificationOptions;
import com.itbaizhan.shopping_common.result.BaseResult;
import com.itbaizhan.shopping_common.service.SpecificationService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/specification")
public class SpecificationController {

    @DubboReference
    private SpecificationService specificationService;

    @PostMapping("/add")
    public BaseResult add(@RequestBody Specification specification){
        specificationService.add(specification);
        return BaseResult.ok();
    }

    @PutMapping("/update")
    public BaseResult update(@RequestBody Specification specification){
        specificationService.update(specification);
        return BaseResult.ok();
    }

    @DeleteMapping("/delete")
    public BaseResult delete(Long[] ids){
        specificationService.delete(ids);
        return BaseResult.ok();
    }

    @GetMapping("/findById")
    public BaseResult<Specification> findById(Long id){
        Specification specification = specificationService.findById(id);
        return BaseResult.ok(specification);
    }

    @GetMapping("/search")
    public BaseResult<Page<Specification>> search(int page, int size){
        Page<Specification> specificationPage = specificationService.search(page, size);
        return BaseResult.ok(specificationPage);
    }

    @GetMapping("/findByProductTypeId")
    public BaseResult<List<Specification>> findByProductTypeId(Long id){
        List<Specification> specificationList = specificationService.findByProductTypeId(id);
        return BaseResult.ok(specificationList);
    }

    @PostMapping("/addOption")
    public BaseResult addOption(@RequestBody SpecificationOptions specificationOptions){
        specificationService.addOption(specificationOptions);
        return BaseResult.ok();
    }

    @DeleteMapping("/deleteOption")
    public BaseResult deleteOption(Long[] ids){
        specificationService.deleteOption(ids);
        return BaseResult.ok();
    }



}
