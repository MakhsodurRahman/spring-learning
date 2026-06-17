package com.example.cache.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        // simple in-memory cache (ConcurrentMap) provided by Spring
        return new MyCacheManager();
    }

    @Bean
    public CacheManager alternateCacheManager() {
        return new ConcurrentMapCacheManager("specialBooks", "otherCaches");
    }

    @Primary
    @Bean("caffeineCacheManager")
    public CaffeineCacheManager caffeineCacheManager() {
        var caffeineCacheMsg = new CaffeineCacheManager();
        caffeineCacheMsg.setCaffeine(
                Caffeine.newBuilder()
                        // Initial size of the cache
                        .initialCapacity(100)
                        
                        // Maximum number of entries the cache may contain
                        // Note: maximumSize and maximumWeight are mutually exclusive
                        .maximumSize(500)
                        // .maximumWeight(1000)
                        // .weigher((key, value) -> 1)
                        
                        // Expire entries after the specified duration since creation or last replacement
                        .expireAfterWrite(Duration.ofSeconds(60))
                        
                        // Expire entries after the specified duration since last read or write
                        // .expireAfterAccess(Duration.ofMinutes(5))
                        
                        // Refresh entries after the specified duration (usually needs a CacheLoader)
                        // .refreshAfterWrite(Duration.ofMinutes(1))
                        
                        // Wrap keys in weak references (allows GC if no longer strongly referenced)
                        // .weakKeys()
                        
                        // Wrap values in weak references
                        // .weakValues()
                        
                        // Wrap values in soft references (GC'd in response to memory demand)
                        // Note: weakValues and softValues are mutually exclusive
                        // .softValues()
                        
                        // Enable recording of cache stats (hit rate, eviction count, etc.)
                        .recordStats()
                        
                        // Listen for entry removals (eviction, invalidation, etc.)
                        .removalListener((key, value, cause) -> {
                            System.out.println("Cache entry removed: key=" + key + ", cause=" + cause);
                        })
        );
        return caffeineCacheMsg;
    }
    @Bean("redisCacheManager")
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(2))
                .disableCachingNullValues();

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration)
                .build();
    }

}
