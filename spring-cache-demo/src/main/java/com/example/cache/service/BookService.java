package com.example.cache.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.CacheAspectSupport;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

   List<String> books = new ArrayList<>();
    public BookService(CacheManager cacheManager) {

    }

    @Cacheable(value = "books",keyGenerator = "myKeyGen")
    public Optional<String> getBookByIsbn(String isbn,boolean flag) {
        return books.stream()
                .filter(name -> name.contains(isbn))
                .findFirst();
    }

    @CacheEvict(value = "books", keyGenerator = "myKeyGen")
    public void evictBook(String isbn) {
        books.add(isbn);
        log.warn("Cache 'books' not available to evict key={}", isbn);
    }

    private void simulateSlowService() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Cacheable(
            value = "liveRates",
            condition = "#type != 'REAL_TIME'"
    )
    public String getRate(String type) {
        return "hello";
    }

    // Example demonstrating the use of 'cacheManager' attribute
    // We are pointing to the 'alternateCacheManager' Bean defined in CacheConfig.java
    @Cacheable(value = "specialBooks", cacheManager = "alternateCacheManager")
    public String getSpecialBook(String isbn) {
        simulateSlowService();
        return "Special Book Detail for ISBN: " + isbn;
    }

    // Example demonstrating the use of Redis Cache
    @Cacheable(value = "redisBooks", cacheManager = "redisCacheManager")
    public String getBookFromRedis(String isbn) {
        log.info("Fetching book from source for ISBN: {}", isbn);
        simulateSlowService();
        return "Redis Book Detail for ISBN: " + isbn;
    }

    // Example demonstrating the use of Caffeine Cache
    @Cacheable(value = "caffeineBooks", cacheManager = "caffeineCacheManager")
    public String getBookFromCaffeine(String isbn) {
        log.info("Fetching book from source (Caffeine) for ISBN: {}", isbn);
        simulateSlowService();
        return "Caffeine Book Detail for ISBN: " + isbn;
    }
}
