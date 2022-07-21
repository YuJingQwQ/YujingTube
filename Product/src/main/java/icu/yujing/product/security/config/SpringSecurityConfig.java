package icu.yujing.product.security.config;

import com.alibaba.fastjson.JSON;
import icu.yujing.common.constant.DomainConstant;
import icu.yujing.common.constant.ExceptionContent;
import icu.yujing.common.utils.R;
import icu.yujing.product.security.exception.AccessDeniedHandlerImpl;
import icu.yujing.product.security.exception.AuthenticationEntryPointImpl;
import icu.yujing.product.security.filters.CheckUserLoginStatusByJwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.PrintWriter;

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

    @Autowired
    private AccessDeniedHandlerImpl accessDeniedHandler;

    @Autowired
    private AuthenticationEntryPointImpl authenticationEntryPoint;

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
                // 鉴权路径
                .antMatchers("/api/author/upload/article/details",
                        "/api/author/reupload/article/*",
                        "/api/author/articles",
                        "/api/comment",
                        "/api/comment/operation/*/*/*").authenticated()
                .anyRequest().permitAll();

        //把token校验过滤器添加到过滤器链中
        http.addFilterBefore(checkUserLoginStatusByJwtFilter, UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);
    }
}
