package com.example.cache;

import com.example.cache.service.BookService;
import com.example.customcache.Bootstrapper;
import com.example.customcache.CacheManager;
import com.example.customcache.ConfigStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;

@SpringBootApplication
@EnableCaching
public class SpringCacheDemoApplication {

    private static final Logger log = LoggerFactory.getLogger(SpringCacheDemoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringCacheDemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner testCacheFeatures(BookService service) {
        return args -> {
            log.info("=========================================");
            log.info("--- Testing Caffeine Cache Setup ---");
            log.info("1st call (Simulating Delay): {}", service.getBookFromCaffeine("CAFF-100"));
            log.info("2nd call (Instant from Cache): {}", service.getBookFromCaffeine("CAFF-100"));
            log.info("=========================================");

            log.info("--- Testing Redis Cache Setup ---");
            try {
                log.info("1st call (Simulating Delay): {}", service.getBookFromRedis("REDIS-200"));
                log.info("2nd call (Instant from Cache): {}", service.getBookFromRedis("REDIS-200"));
            } catch (Exception e) {
                log.warn("Redis Cache Test Failed (Is Redis running locally?): {}", e.getMessage());
            }
            log.info("=========================================");
        };
    }

    @Bean
    public CommandLineRunner initCustomCache(BookService bookService) {
        return args -> {
            CacheManager manager = new CacheManager();
            ConfigStore store = new ConfigStore();
            Path cfg = Path.of("src/main/resources/custom-cache.properties");
            Bootstrapper.initialize(cfg, manager, store);
            
            log.info("Custom caches initialized: books={}", (manager.getCache("books") != null));

            bookService.evictBook("book1");
            log.info("Result for book1: {}", bookService.getBookByIsbn("book1", true));
        };
    }
}
