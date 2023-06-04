package com.itbaizhan.shopping_admin_service.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.shopping_admin_service.mapper.AdminMapper;
import com.itbaizhan.shopping_common.pojo.Admin;
import com.itbaizhan.shopping_common.pojo.Permission;
import com.itbaizhan.shopping_common.service.AdminService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@DubboService
@Transactional
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public void add(Admin admin) {
        adminMapper.insert(admin);
    }

    @Override
    public void update(Admin admin) {
        // 如果密码为空则密码还是原来的密码
        if(!StringUtils.hasText(admin.getPassword())){
            // 查询出原来的密码
            String password = adminMapper.selectById(admin.getAid()).getPassword();
            admin.setPassword(password);
        }
        adminMapper.updateById(admin);
    }

    @Override
    public void delete(Long id) {
        // 删除管理员所拥有的角色
        adminMapper.deleteAdminAllRole(id);
        // 删除管理员
        adminMapper.deleteById(id);
    }

    @Override
    public Admin findById(Long id) {
        return adminMapper.findById(id);
    }

    @Override
    public Page<Admin> search(int page, int size) {
        Page selectPage = adminMapper.selectPage(new Page(page, size), null);
        return selectPage;
    }

    @Override
    public void updateRoleToAdmin(Long aid, Long[] rids) {
        // 删除管理员_角色表中的数据
        adminMapper.deleteAdminAllRole(aid);
        // 给管理员重新添加角色
        for (Long rid : rids) {
            adminMapper.addRoleToAdmin(aid,rid);
        }

    }

    @Override
    public Admin findByUsername(String username) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username",username);
        Admin admin = adminMapper.selectOne(queryWrapper);
        return admin;
    }

    @Override
    public List<Permission> findByUsernameToPermission(String username) {
        return adminMapper.findByUsernameToPermission(username);
    }
}
