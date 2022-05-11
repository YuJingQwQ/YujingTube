package icu.yujing.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@EnableDiscoveryClient
@EnableFeignClients("icu.yujing.thirdparty.app.controller")
@SpringBootApplication
public class ThirdParty5003Application {
    public static void main(String[] args) {
        SpringApplication.run(ThirdParty5003Application.class, args);
    }
}
