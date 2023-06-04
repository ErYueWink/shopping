package com.itbaizhan.shopping_admin_service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itbaizhan.shopping_common.pojo.Admin;
import com.itbaizhan.shopping_common.pojo.Permission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AdminMapper extends BaseMapper<Admin> {

    // 删除管理员所有角色(admin_role)
    void deleteAdminAllRole(Long aid);

    // 根据id查询管理员
    Admin findById(Long id);

    // 新增管理员角色
    void addRoleToAdmin(@Param("aid") Long aid, @Param("rid") Long rid);

    // 根据用户名查询权限
    List<Permission> findByUsernameToPermission(String username);
}
