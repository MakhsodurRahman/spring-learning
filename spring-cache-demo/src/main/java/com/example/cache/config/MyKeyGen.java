package com.example.cache.config;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class MyKeyGen implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {

        if (params.length == 0) {
            return SimpleKey.EMPTY;
        }

        if (params.length == 1 && params[0] != null) {
            return params[0];
        }

        return new SimpleKey(params);
    }
}
