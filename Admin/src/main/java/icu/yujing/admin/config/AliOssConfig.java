package icu.yujing.admin.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import icu.yujing.common.properties.AliOssProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@Configuration
public class AliOssConfig {

    @Bean
    public OSS ossClient() {
        return new OSSClientBuilder().build(AliOssProperties.endpoint, AliOssProperties.accessKeyId, AliOssProperties.accessKeySecret);
    }
}
