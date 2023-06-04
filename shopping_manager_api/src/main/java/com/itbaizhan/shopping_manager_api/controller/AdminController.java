package com.itbaizhan.shopping_manager_api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itbaizhan.shopping_common.pojo.Admin;
import com.itbaizhan.shopping_common.result.BaseResult;
import com.itbaizhan.shopping_common.service.AdminService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @DubboReference
    private AdminService adminService;
    @Autowired
    private PasswordEncoder encoder;
    /**
     * 新增管理员
     * @param admin 管理员对象
     * @return
     */
    @PostMapping("/add")
    public BaseResult add(@RequestBody Admin admin){
        // 对密码进行加密
        String password = admin.getPassword();
        password = encoder.encode(password);
        admin.setPassword(password);
        adminService.add(admin);
        return BaseResult.ok();
    }

    /**
     * 修改管理员
     * @param admin 管理员对象
     * @return
     */
    @PutMapping("/update")
    public BaseResult update(@RequestBody Admin admin){
        String password = admin.getPassword();
        if(StringUtils.hasText(password)){ // 如果传来的密码不为空，则对密码进行加密
            password = encoder.encode(password);
            admin.setPassword(password);
        }
        adminService.update(admin);
        return BaseResult.ok();
    }

    /**
     * 删除管理员
     * @param aid 管理员id
     * @return
     */
    @DeleteMapping("/delete")
    public BaseResult delete(Long aid){
        adminService.delete(aid);
        return BaseResult.ok();
    }

    /**
     * 根据id查询管理员
     * @param aid 管理员id
     * @return 查询结果
     */
    @GetMapping("/findById")
    public BaseResult<Admin> findById(Long aid){
        Admin admin = adminService.findById(aid);
        return BaseResult.ok(admin);
    }

    /**
     * 分页查询管理员
     * @param page 页数
     * @param size 每页条数
     * @return 查询结果
     */
    @PreAuthorize("hasAnyAuthority('/admin/search')")
    @GetMapping("/search")
    public BaseResult<Page<Admin>> search(int page, int size){
        Page<Admin> adminPage = adminService.search(page, size);
        return BaseResult.ok(adminPage);
    }

    /**
     * 修改管理员角色
     * @param aid 管理员id
     * @param rids 角色id集合
     * @return
     */
    @PutMapping("/updateRoleToAdmin")
    public BaseResult updateRoleToAdmin(Long aid,Long[] rids){
        adminService.updateRoleToAdmin(aid,rids);
        return BaseResult.ok();
    }

    @GetMapping("/getUserName")
    public BaseResult<String> getUserName(){
        // 获取会话对象
        SecurityContext securityContext = SecurityContextHolder.getContext();
        // 获取认证对象
        Authentication authentication = securityContext.getAuthentication();
        // 获取认证信息
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // 返回用户名
        String username = userDetails.getUsername();
        return BaseResult.ok(username);
    }

}
