package icu.yujing.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@Component
@ConfigurationProperties("ali.oss")
public class AliOssProperties {
    public static String endpoint = "oss-cn-guangzhou.aliyuncs.com";
    public static String accessKeyId = "yourAccessKeyId";
    public static String accessKeySecret = "yourAccessKeySecret";
    public static String bucketName = "yujing-youtube";

    public static String aliOssAddress = "https://yujing-youtube.oss-cn-guangzhou.aliyuncs.com/";
}
