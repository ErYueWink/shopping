package com.itbaizhan.shopping_common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.shopping_common.pojo.Admin;
import com.itbaizhan.shopping_common.pojo.Permission;
import com.itbaizhan.shopping_common.pojo.Role;

import java.util.List;

// 角色服务接口
public interface RoleService {

    // 新增角色
    void add(Role role);
    // 修改角色
    void update(Role role);
    // 删除角色
    void delete(Long id);
    // 根据id查询角色
    Role findById(Long id);
    // 查询所有角色(该方法用于用户服务中的预查询)
    List<Role> findAll();
    // 分页查询所有角色
    Page<Role> search(int page,int size);
    // 修改角色对应的权限
    void updatePermissionToRole(Long rid,Long[] pids);

}
