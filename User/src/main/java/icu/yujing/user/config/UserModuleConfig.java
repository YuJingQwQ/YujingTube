package icu.yujing.user.config;

import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@Configuration
@EnableDiscoveryClient
@EnableAutoDataSourceProxy
@EnableFeignClients(basePackages = "icu.yujing.user.feign")
@EnableDubbo(scanBasePackages = "icu.yujing")
public class UserModuleConfig {
    public static final BCryptPasswordEncoder USER_PASSWORD_ENCODER = new BCryptPasswordEncoder();
}
