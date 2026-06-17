package com.example.customcache;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

public class Bootstrapper {
    public static void initialize(Path configPath, CacheManager manager, ConfigStore store) throws Exception {
        ConfigLoader loader = new ConfigLoader();
        Map<String, String> props = loader.loadProperties(configPath);
        store.addPropertySource(props);

        // read cache.names
        String names = store.getOrDefault("cache.names", "");
        if (names.isBlank()) return;
        SimpleBinder binder = new SimpleBinder(store);
        for (String name : names.split(",")) {
            name = name.trim();
            CacheConfig cfg = new CacheConfig();
            cfg.setName(name);
            String ttl = store.getOrDefault("cache." + name + ".ttl", "30");
            String max = store.getOrDefault("cache." + name + ".maxSize", "1000");
            cfg.setTtlSeconds(Long.parseLong(ttl));
            cfg.setMaxSize(Integer.parseInt(max));
            manager.createCache(cfg);
        }
    }
}
