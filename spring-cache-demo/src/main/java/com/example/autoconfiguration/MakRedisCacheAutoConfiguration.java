package com.example.autoconfiguration;


import com.example.cache.annotation.RedisCacheableProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

@AutoConfiguration
@ConditionalOnClass(org.springframework.cache.CacheManager.class)
@ConditionalOnProperty(prefix = "mak.redis.cache",name = "enabled",havingValue = "true")
public class MakRedisCacheAutoConfiguration {

    @Bean
    public RedisCacheableProcessor redisCacheableProcessor(RedisTemplate<Object, Object> redisTemplate, ApplicationContext applicationContext) {
        return new RedisCacheableProcessor(redisTemplate, applicationContext);
    }
}
