package com.itbaizhan.shopping_admin_service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itbaizhan.shopping_common.pojo.Role;
import org.apache.ibatis.annotations.Param;

public interface RoleMapper extends BaseMapper<Role> {
    // 根据id查询角色以及对应的权限
    Role findById(Long id);

    // 删除角色_权限表中的数据
    void deleteAllPermission(Long rid);
    // 新增角色_权限表中的数据
    void addPermissionToRole(@Param("rid") Long rid, @Param("pid") Long pid);
}
