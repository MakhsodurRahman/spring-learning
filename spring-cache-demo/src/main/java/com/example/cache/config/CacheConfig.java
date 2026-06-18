package com.example.cache.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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

    @Bean("sdlcProCacheResolver")
    public CacheResolver sdlcProCacheResolver(CaffeineCacheManager caffeineCacheManager, RedisCacheManager redisCacheManager) {
        return new CacheResolver() {
            @Override
            public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
                // Get the first argument as product/book ID or ISBN
                Object[] args = context.getArgs();
                if (args.length > 0 && args[0] != null) {
                    String identifier = args[0].toString();
                    return isHotProduct(identifier) ? buildCache(caffeineCacheManager, context) : buildCache(redisCacheManager, context);
                }
                // Fallback to caffeineCacheManager
                return buildCache(caffeineCacheManager, context);
            }

            private boolean isHotProduct(String identifier) {
                // Product is hot if its identifier starts with "CAFF" or "HOT" or is "book1"
                return identifier != null && (identifier.startsWith("CAFF") || identifier.startsWith("HOT") || "book1".equals(identifier));
            }

            private Collection<Cache> buildCache(CacheManager cacheManager, CacheOperationInvocationContext<?> context) {
                Collection<String> cacheNames = context.getOperation().getCacheNames();
                List<Cache> caches = new ArrayList<>();
                if (cacheNames.isEmpty()) {
                    Cache defaultCache = cacheManager.getCache("books");
                    if (defaultCache != null) {
                        caches.add(defaultCache);
                    }
                } else {
                    for (String name : cacheNames) {
                        Cache cache = cacheManager.getCache(name);
                        if (cache != null) {
                            caches.add(cache);
                        }
                    }
                }
                return caches;
            }
        };
    }

}
