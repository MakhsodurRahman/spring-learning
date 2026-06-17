package com.example.customcache;

import java.util.HashMap;
import java.util.Map;

public class CacheManager {
    private final Map<String, SimpleCache<Object, Object>> caches = new HashMap<>();

    public SimpleCache<Object, Object> getCache(String name) {
        return caches.get(name);
    }

    public void createCache(CacheConfig cfg) {
        SimpleCache<Object, Object> cache = new SimpleCache<>(cfg);
        caches.put(cfg.getName(), cache);
    }
}
