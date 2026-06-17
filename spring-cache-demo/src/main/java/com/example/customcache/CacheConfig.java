package com.example.customcache;

public class CacheConfig {
    private String name;
    private long ttlSeconds = 30;
    private int maxSize = 1000;

    public CacheConfig() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public long getTtlSeconds() { return ttlSeconds; }
    public void setTtlSeconds(long ttlSeconds) { this.ttlSeconds = ttlSeconds; }
    public int getMaxSize() { return maxSize; }
    public void setMaxSize(int maxSize) { this.maxSize = maxSize; }
}
