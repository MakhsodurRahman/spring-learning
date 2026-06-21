package com.example.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCacheable {

    String key() default "";
    String cacheName() default "";
    long ttl() default 0;
    TimeUnit ttlUnit() default TimeUnit.MILLISECONDS;
}
