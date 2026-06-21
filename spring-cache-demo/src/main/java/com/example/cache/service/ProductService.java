package com.example.cache.service;


import com.example.cache.annotation.RedisCacheable;
import com.example.cache.entity.Product;
import com.example.cache.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;


//    @Cacheable(cacheNames = "product", unless = "#result?.stock == null")
    @RedisCacheable(cacheName = "product", key = "#id", ttl = 60, ttlUnit = TimeUnit.SECONDS)
    public Product get(Long id){
        return productRepository.findById(id).orElse(null);
    }
}
