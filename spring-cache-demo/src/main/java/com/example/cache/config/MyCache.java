package com.example.cache.config;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MyCache implements Cache {

    private final String name;
    private final ConcurrentMap<Object, Object> store;

    public MyCache(String name) {
        this.name = name;
        this.store = new ConcurrentHashMap<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return null;
    }

    @Override
    public ValueWrapper get(Object key) {

        Object value = store.get(key);

        return value != null ? new SimpleValueWrapper(value) : null;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return null;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return null;
    }

    @Override
    public void put(Object key, Object value) {
        this.store.put(key,value);
    }

    @Override
    public void evict(Object key) {

    }

    @Override
    public void clear() {

    }
}
