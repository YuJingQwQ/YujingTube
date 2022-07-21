package icu.yujing.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author: Cyqurt
 * @version: 1.0
 * @date: 2022/1/4 14:35
 */
@SpringBootApplication
public class Gateway5000Application {
    public static void main(String[] args) {
        SpringApplication.run(Gateway5000Application.class, args);
    }
}
