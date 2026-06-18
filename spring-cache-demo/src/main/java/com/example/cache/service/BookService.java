package com.example.cache.service;

import com.example.cache.entity.Book;
import com.example.cache.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Cacheable(value = "books", keyGenerator = "myKeyGen")
    public Optional<Book> getBookByIsbn(String isbn, boolean flag) {
        log.info("Fetching book by ISBN {} from DB (Default Cache)", isbn);
        simulateSlowService();
        return bookRepository.findByIsbn(isbn);
    }

    @Transactional
    @CacheEvict(value = "books", keyGenerator = "myKeyGen")
    public void evictBook(String isbn) {
        log.info("Evicting and deleting book by ISBN {} from DB", isbn);
        bookRepository.deleteByIsbn(isbn);
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
    @Cacheable(value = "redisBooks", cacheManager = "redisCacheManager",key = "#isbn")
    public Book getBookFromRedis(String isbn) {
        log.info("Fetching book from DB (Redis Cacheable) for ISBN: {}", isbn);
        simulateSlowService();
        return bookRepository.findByIsbn(isbn).orElse(null);
    }

    // Example demonstrating the use of Caffeine Cache
    @Cacheable(value = "caffeineBooks", cacheManager = "caffeineCacheManager")
    public Book getBookFromCaffeine(String isbn) {
        log.info("Fetching book from DB (Caffeine Cacheable) for ISBN: {}", isbn);
        simulateSlowService();
        return bookRepository.findByIsbn(isbn).orElse(null);
    }

    @Transactional
    public Book saveBook(Book book) {
        log.info("Saving book with ISBN {} to DB", book.getIsbn());
        return bookRepository.save(book);
    }

    @Cacheable(cacheResolver = "sdlcProCacheResolver")
    public Book getBookWithDynamicCache(String isbn) {
        log.info("Fetching book from DB (Dynamic Cache Resolver) for ISBN: {}", isbn);
        simulateSlowService();
        return bookRepository.findByIsbn(isbn).orElse(null);
    }
}
