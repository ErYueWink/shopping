package com.itbaizhan.shopping_manager_api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.shopping_common.pojo.Role;
import com.itbaizhan.shopping_common.result.BaseResult;
import com.itbaizhan.shopping_common.service.RoleService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {

    @DubboReference
    private RoleService roleService;

    @PostMapping("/add")
    public BaseResult add(@RequestBody Role role){
        roleService.add(role);
        return BaseResult.ok();
    }

    @PutMapping("/update")
    public BaseResult update(@RequestBody Role role){
        roleService.update(role);
        return BaseResult.ok();
    }

    @DeleteMapping("/delete")
    public BaseResult delete(Long rid){
        roleService.delete(rid);
        return BaseResult.ok();
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('/role/search')")
    public BaseResult<Page<Role>> search(int page, int size){
        Page<Role> rolePage = roleService.search(page, size);
        return BaseResult.ok(rolePage);
    }

    @GetMapping("/findAll")
    public BaseResult<List<Role>> all(){
        List<Role> roleList = roleService.findAll();
        return BaseResult.ok(roleList);
    }
    @GetMapping("/findById")
    public BaseResult<Role> findById(Long rid){
        Role role = roleService.findById(rid);
        return BaseResult.ok(role);
    }

    @PutMapping("/updatePermissionToRole")
    public BaseResult updatePermissionToRole(Long rid,Long[] pids){
        roleService.updatePermissionToRole(rid,pids);
        return BaseResult.ok();
    }
}
