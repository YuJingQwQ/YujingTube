package icu.yujing.user.security.config;

import icu.yujing.common.constant.DomainConstant;
import icu.yujing.user.security.filters.CheckUserLoginStatusByJwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author: Cyqurt
 * @date: 2022/4/15
 * @version: 1.0
 * @description:
 */
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Import(CheckUserLoginStatusByJwtFilter.class)
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private CheckUserLoginStatusByJwtFilter checkUserLoginStatusByJwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //关闭csrf
                .csrf().disable()
                //不通过Session获取SecurityContext
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin()
                .loginPage(DomainConstant.PRODUCT + "/login")
                .and()
                .authorizeRequests()
                // 对于登录接口 允许匿名访问
                .antMatchers(
                        // userLoginApiController
                        "/api/user/list/all",
                        "/api/user/register",
                        "/api/user/verification/code",
                        "/api/user/upload/avatar",
                        // userApiController
                        "/api/user/from/session",
                        "/api/users/avatar/nickname",
                        "/api/user/*").permitAll()
                .antMatchers("/api/user/login/by/password",
                        "/api/user/login/by/code").anonymous()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated();

        //把token校验过滤器添加到过滤器链中
        http.addFilterBefore(checkUserLoginStatusByJwtFilter, UsernamePasswordAuthenticationFilter.class);

    }

}
