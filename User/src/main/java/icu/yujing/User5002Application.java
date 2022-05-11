package icu.yujing;

import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author: Cyqurt
 * @date: 2022/3/17
 * @version: 1.0
 * @description:
 */
@EnableDiscoveryClient
@EnableAutoDataSourceProxy
@EnableFeignClients(basePackages = "icu.yujing.user.feign")
@SpringBootApplication
public class User5002Application {
    public static void main(String[] args) {
        SpringApplication.run(User5002Application.class, args);
    }
}
