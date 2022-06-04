package icu.yujing.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@EnableRedisHttpSession
@Configuration
public class CommonSpringSessionConfig {
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new Jackson2JsonRedisSerializer<Object>(Object.class);
    }

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("YUJINGSESSION");
        serializer.setDomainName("yujing.icu");
        return serializer;
    }
}
