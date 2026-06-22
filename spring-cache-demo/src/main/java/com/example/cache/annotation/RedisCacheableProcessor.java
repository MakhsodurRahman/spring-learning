package com.example.cache.annotation;

import io.lettuce.core.dynamic.annotation.CommandNaming;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
//@Component
public class RedisCacheableProcessor {

    private static final String KEY_SEPARATOR = ":";

    private final RedisTemplate<Object, Object> redisTemplate;
    private final ApplicationContext applicationContext;

    public RedisCacheableProcessor(RedisTemplate<Object, Object> redisTemplate, ApplicationContext applicationContext) {
        this.redisTemplate = redisTemplate;
        this.applicationContext = applicationContext;
    }

    @Around(value = "@annotation(com.example.cache.annotation.RedisCacheable)")
    public Object cacheableProcessor(ProceedingJoinPoint pjp) throws Throwable {

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        pjp.getSignature();

        String[] parameterNames = signature.getParameterNames();
        Object[] args = pjp.getArgs();
        RedisCacheable redisCacheable = signature.getMethod().getAnnotation(RedisCacheable.class);

        String cacheName = redisCacheable.cacheName();
        Object cacheKey = evaluateExpression(redisCacheable.key(), parameterNames, args);

        String finalKey = cacheName + KEY_SEPARATOR + cacheKey;

        var valOps = redisTemplate.opsForValue();
        Object cachedValue = valOps.get(finalKey);
        if (cachedValue != null) {
            return cachedValue;
        }


        Object targetValue = pjp.proceed();
        long ttl = redisCacheable.ttl();
        if (ttl > 0) {
            valOps.set(finalKey, targetValue, ttl, redisCacheable.ttlUnit());
        } else {
            valOps.set(finalKey, targetValue);
        }
        return targetValue;
    }

    private Object evaluateExpression(String expression, String[] paramNames, Object[] args) throws Throwable {

        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        
        // Setting BeanResolver to allow SpEL expressions to invoke other Spring Beans.
        // This enables powerful dynamic keys, e.g., @RedisCacheable(key = "@myService.generateKey(#id)")
        context.setBeanResolver(new BeanFactoryResolver(applicationContext));
        
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        return parser.parseExpression(expression).getValue(context);
    }
}

