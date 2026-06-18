package com.example.cache;

import com.example.cache.entity.Book;
import com.example.cache.service.BookService;
import com.example.cache.service.ProductService;
import com.example.customcache.Bootstrapper;
import com.example.customcache.CacheManager;
import com.example.customcache.ConfigStore;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class SpringCacheDemoApplication {

    private static final Logger log = LoggerFactory.getLogger(SpringCacheDemoApplication.class);
    private final ProductService productService;

    public static void main(String[] args) {
        SpringApplication.run(SpringCacheDemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner runDemo(BookService service) {
        return args -> {
            log.info("--- 1. Database Initialization ---");
            service.saveBook(new Book("CAFF-100", "Effective Java", "Joshua Bloch"));
            service.saveBook(new Book("REDIS-200", "Spring Microservices in Action", "John Carnell"));
            service.saveBook(new Book("book1", "Clean Code", "Robert C. Martin"));

            log.info("=========================================");
            log.info("--- 2. Testing Caffeine Cache Setup ---");
            log.info("1st call (Simulating Delay): {}", service.getBookFromCaffeine("CAFF-100"));
            log.info("2nd call (Instant from Cache): {}", service.getBookFromCaffeine("CAFF-100"));
            log.info("=========================================");

            log.info("--- 3. Testing Redis Cache Setup ---");
            try {
                log.info("1st call (Simulating Delay): {}", service.getBookFromRedis("REDIS-200"));
                log.info("2nd call (Instant from Cache): {}", service.getBookFromRedis("REDIS-200"));
            } catch (Exception e) {
                log.warn("Redis Cache Test Failed (Is Redis running locally?): {}", e.getMessage());
            }
            log.info("=========================================");

            log.info("--- 4. Custom Cache Initialization & Invalidation Test ---");
            CacheManager manager = new CacheManager();
            ConfigStore store = new ConfigStore();
            Path cfg = Path.of("src/main/resources/custom-cache.properties");
            Bootstrapper.initialize(cfg, manager, store);
            
            log.info("Custom caches initialized: books={}", (manager.getCache("books") != null));

            // Test default database-backed cache setup
            log.info("Fetching book1 (1st call, simulating delay): {}", service.getBookByIsbn("book1", true).orElse(null));
            log.info("Fetching book1 (2nd call, instant from cache): {}", service.getBookByIsbn("book1", true).orElse(null));
            
            service.evictBook("book1");
            log.info("Fetching book1 after eviction (Should be Not Found because evict also deletes from DB): {}", 
                     service.getBookByIsbn("book1", true).orElse(null));

            // Test dynamic cache resolver routing
            log.info("=========================================");
            log.info("--- 5. Testing Dynamic Cache Resolver Routing ---");
            log.info("[Dynamic Resolver] Fetching 'CAFF-100' (should route to Caffeine, simulating delay): {}", 
                     service.getBookWithDynamicCache("CAFF-100"));
            log.info("[Dynamic Resolver] Fetching 'CAFF-100' (should route to Caffeine, instant from cache): {}", 
                     service.getBookWithDynamicCache("CAFF-100"));
            
            try {
                log.info("[Dynamic Resolver] Fetching 'REDIS-200' (should route to Redis, simulating delay): {}", 
                         service.getBookWithDynamicCache("REDIS-200"));
                log.info("[Dynamic Resolver] Fetching 'REDIS-200' (should route to Redis, instant from cache): {}", 
                         service.getBookWithDynamicCache("REDIS-200"));
            } catch (Exception e) {
                log.warn("[Dynamic Resolver] Redis Cache Resolution Test Failed: {}", e.getMessage());
            }
            log.info("=========================================");

            productService.get(1L);
            log.info("get the product");
            Thread.sleep(30000);
            productService.get(1L);
            log.info("get the product ::: {}", productService.get(1L).getProductName());
            System.out.println("test");
        };
    }
}
