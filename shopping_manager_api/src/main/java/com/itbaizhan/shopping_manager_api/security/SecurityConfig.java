package com.itbaizhan.shopping_manager_api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// Security配置类
@Configuration
// 开启鉴权配置
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 自定义表单登录
        http.formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginProcessingUrl("/admin/login")
                .successHandler(new MyLoginSuccessHandler()) // 登录成功处理器
                .failureHandler(new MyLoginFailureHandler()); // 登录失败处理器

        // 权限拦截配置
        http.authorizeRequests()
                .antMatchers("/admin/login").permitAll() // 登录请求不需要认证
                .antMatchers("/login").permitAll() //登录页不需要认证
                .anyRequest().authenticated(); // 其余都需要认证

        // 退出登录配置
        http.logout()
                .logoutUrl("/admin/logout")
                .logoutSuccessHandler(new MyLogoutAccessHandler()) // 退出成功处理器
                .clearAuthentication(true) // 清除认证数据
                .invalidateHttpSession(true); // 销毁session

        // 异常处理
        http.exceptionHandling()
                .authenticationEntryPoint(new MyAuthenticationEntryPoint()) // 用户未登录处理器
                .accessDeniedHandler(new MyAccessDeniedHandler()); // 权限不足处理器

        // 关闭csrf防护
        http.csrf().disable();
        // 开启跨域访问
        http.cors();

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
    }
}

