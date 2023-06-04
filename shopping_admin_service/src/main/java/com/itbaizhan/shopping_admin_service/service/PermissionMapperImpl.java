package com.itbaizhan.shopping_admin_service.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.shopping_admin_service.mapper.PermissionMapper;
import com.itbaizhan.shopping_common.pojo.Permission;
import com.itbaizhan.shopping_common.service.PermissionService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@DubboService
@Transactional
public class PermissionMapperImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public void add(Permission permission) {
        permissionMapper.insert(permission);
    }

    @Override
    public void update(Permission permission) {
        permissionMapper.updateById(permission);
    }

    @Override
    public void delete(Long id) {
        permissionMapper.deleteById(id);
    }

    @Override
    public Page<Permission> search(int page, int size) {
        return permissionMapper.selectPage(new Page(page,size),null);
    }

    @Override
    public Permission findById(Long id) {
        return permissionMapper.selectById(id);
    }

    @Override
    public List<Permission> findAll() {
        return permissionMapper.selectList(null);
    }
}
