package icu.yujing.product.config;

import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@EnableScheduling
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "icu.yujing.product.feign")
@EnableAutoDataSourceProxy
@Configuration
public class ProductModuleConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(6,
                6,
                0,
                TimeUnit.DAYS,
                new LinkedBlockingQueue<>());
    }

    @Bean
    public RedissonClient redissonClient() {
        // 1. Create config object
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.89.128:6379");

        // 2. Create Redisson instance
        // Sync and Async API
        return Redisson.create(config);
    }

}
