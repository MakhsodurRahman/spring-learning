package com.example.customcache;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleCache<K, V> {
    record Entry<V>(V value, long expiresAt) {}

    private final Map<K, Entry<V>> map = new ConcurrentHashMap<>();
    private final CacheConfig config;

    public SimpleCache(CacheConfig config) {
        this.config = config;
    }

    public V get(K key) {
        Entry<V> e = map.get(key);
        if (e == null) return null;
        if (e.expiresAt < Instant.now().getEpochSecond()) {
            map.remove(key);
            return null;
        }
        return e.value;
    }

    public void put(K key, V value) {
        long expiresAt = Instant.now().getEpochSecond() + config.getTtlSeconds();
        map.put(key, new Entry<>(value, expiresAt));
        if (map.size() > config.getMaxSize()) {
            // naive eviction: clear oldest entry (not efficient)
            map.keySet().stream().findFirst().ifPresent(map::remove);
        }
    }

    public void evict(K key) { map.remove(key); }
    public void clear() { map.clear(); }
}
