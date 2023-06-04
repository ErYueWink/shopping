package com.itbaizhan.shopping_admin_service.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.shopping_admin_service.mapper.RoleMapper;
import com.itbaizhan.shopping_common.pojo.Role;
import com.itbaizhan.shopping_common.service.RoleService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@DubboService
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public void add(Role role) {
        roleMapper.insert(role);
    }

    @Override
    public void update(Role role) {
        roleMapper.updateById(role);
    }

    @Override
    public void delete(Long id) {
        // 删除角色_权限表中的数据
        roleMapper.deleteAllPermission(id);
        // 删除角色
        roleMapper.deleteById(id);
    }

    @Override
    public Role findById(Long id) {
        return roleMapper.findById(id);
    }

    @Override
    public List<Role> findAll() {
        return roleMapper.selectList(null);
    }

    @Override
    public Page<Role> search(int page, int size) {
        return roleMapper.selectPage(new Page(page,size),null);
    }

    @Override
    public void updatePermissionToRole(Long rid, Long[] pids) {
        // 删除角色所有权限
        roleMapper.deleteAllPermission(rid);
        // 给角色重新添加权限
        for (Long pid : pids) {
            roleMapper.addPermissionToRole(rid,pid);
        }
    }
}
