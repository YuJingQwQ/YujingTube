package icu.yujing.product.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@Configuration
public class ProductRedisConfig {
    /**
     * Redis连接工厂
     *
     * @return
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("192.168.89.128", 6379));
    }

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        FastJsonRedisSerializer jsonRedisSerializer = new FastJsonRedisSerializer(Object.class);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 至关重要! 将默认序列化器设置为JSON序列化器
        redisTemplate.setDefaultSerializer(jsonRedisSerializer);
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setStringSerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(jsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jsonRedisSerializer);
        return redisTemplate;
    }

//    @Bean
//    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        Jackson2JsonRedisSerializer jsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
//        StringRedisTemplate redisTemplate = new StringRedisTemplate();
//        redisTemplate.setConnectionFactory(redisConnectionFactory);
//        // 至关重要! 将默认序列化器设置为JSON序列化器
//        redisTemplate.setDefaultSerializer(jsonRedisSerializer);
//        redisTemplate.setKeySerializer(stringRedisSerializer);
//        redisTemplate.setStringSerializer(stringRedisSerializer);
//        redisTemplate.setHashKeySerializer(stringRedisSerializer);
//        redisTemplate.setValueSerializer(jsonRedisSerializer);
//        redisTemplate.setHashValueSerializer(jsonRedisSerializer);
//        return redisTemplate;
//    }

    /**
     * 配置使用SpringCache相关注解时,序列化失败问题
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisTemplate redisTemplate) {
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisTemplate.getConnectionFactory());
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisTemplate.getValueSerializer()));
        // 默认过期时间
        redisCacheConfiguration.entryTtl(Duration.ofDays(30));
        return new RedisCacheManager(redisCacheWriter, redisCacheConfiguration);
    }
}
