package com.example.customcache;

import java.lang.reflect.Field;
import java.util.Locale;

public class SimpleBinder {

    private final ConfigStore store;

    public SimpleBinder(ConfigStore store) {
        this.store = store;
    }

    public <T> T bind(String prefix, Class<T> type) {
        try {
            T instance = type.getDeclaredConstructor().newInstance();
            for (Field f : type.getDeclaredFields()) {
                f.setAccessible(true);
                String key = prefix + "." + f.getName();
                String val = store.get(key).orElse(null);
                if (val == null && f.getName().equals("ttlSeconds")) {
                    val = store.get(prefix + ".ttl").orElse(null);
                }
                if (val != null) {
                    Object converted = convert(val, f.getType());
                    f.set(instance, converted);
                }
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object convert(String val, Class<?> target) {
        if (target == String.class) return val;
        if (target == int.class || target == Integer.class) return Integer.parseInt(val);
        if (target == long.class || target == Long.class) return Long.parseLong(val);
        if (target == boolean.class || target == Boolean.class) return Boolean.parseBoolean(val);
        if (target == double.class || target == Double.class) return Double.parseDouble(val);
        // add more conversions as needed
        return null;
    }
}
