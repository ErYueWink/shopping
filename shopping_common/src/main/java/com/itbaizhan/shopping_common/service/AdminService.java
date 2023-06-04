package com.itbaizhan.shopping_common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.shopping_common.pojo.Admin;
import com.itbaizhan.shopping_common.pojo.Permission;

import java.util.List;

// 后台用户服务接口
public interface AdminService {

    // 新增管理员
    void add(Admin admin);
    // 修改管理员
    void update(Admin admin);
    // 删除管理员
    void delete(Long id);
    // 根据id查询管理员
    Admin findById(Long id);
    // 分页查询所有管理员
    Page<Admin> search(int page,int size);
    // 修改管理员角色
    void updateRoleToAdmin(Long aid,Long[] rids);

    // 根据用户名查询管理员
    Admin findByUsername(String username);
    // 根据用户名查询权限
    List<Permission> findByUsernameToPermission(String username);

}
