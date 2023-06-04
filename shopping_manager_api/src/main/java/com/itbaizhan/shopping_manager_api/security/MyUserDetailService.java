package com.itbaizhan.shopping_manager_api.security;

import com.itbaizhan.shopping_common.pojo.Admin;
import com.itbaizhan.shopping_common.pojo.Permission;
import com.itbaizhan.shopping_common.service.AdminService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MyUserDetailService implements UserDetailsService {

    @DubboReference
    private AdminService adminService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 认证
        Admin admin = adminService.findByUsername(username);
        if(admin == null){
            throw new UsernameNotFoundException("用户名不存在");
        }
        // 授权
        List<Permission> permissionList = adminService.findByUsernameToPermission(username);
        List<GrantedAuthority> list = new ArrayList();
        // 将查询到的权限封装为GrantedAuthority对象
        for (Permission permission : permissionList) {
            list.add(new SimpleGrantedAuthority(permission.getUrl()));
        }
        UserDetails userDetails = User.withUsername(username)
                .password(admin.getPassword())
                .authorities(list)
                .build();
        return userDetails;
    }
}
