package icu.yujing.admin.config;

import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: Cyqurt
 * @date: 2022/4/13
 * @version: 1.0
 * @description:
 */
@EnableAutoDataSourceProxy
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "icu.yujing.admin.feign")
@Configuration
public class AdminModuleConfig {
    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(6,
                6,
                0,
                TimeUnit.DAYS,
                new LinkedBlockingQueue<>());
    }


}
