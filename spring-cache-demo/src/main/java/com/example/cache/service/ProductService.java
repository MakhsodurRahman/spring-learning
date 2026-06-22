package com.example.cache.service;


import com.example.cache.annotation.RedisCacheable;
import com.example.cache.entity.Product;
import com.example.cache.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

//    @Cacheable(cacheNames = "product", unless = "#result?.stock == null")
    @RedisCacheable(cacheName = "product", key = "@productKeyGen.generateKey(#id)", ttl = 60, ttlUnit = TimeUnit.SECONDS)
    public Product get(Long id){
        log.info("Fetching Product with ID: {} from Database (Cache Miss / DB Hit)", id);
        return productRepository.findById(id).orElse(null);
    }
}

@Component
class ProductKeyGen {
    public String generateKey(Integer productId) {
        return "pid-" + productId;
    }
}

