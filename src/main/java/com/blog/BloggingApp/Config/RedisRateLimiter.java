package com.blog.BloggingApp.Config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class RedisRateLimiter {

    private final RedisTemplate<String, Integer> redisTemplate;
    private final int MAX_REQUESTS = 100; // Max requests per time window
    private final Duration TIME_WINDOW = Duration.ofMinutes(1); // Time window duration

    public RedisRateLimiter(@Qualifier("integerRedisTemplate") RedisTemplate<String, Integer> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String key) {
        Integer requests = redisTemplate.opsForValue().get(key);

        if (requests == null) {
            redisTemplate.opsForValue().set(key, 1, TIME_WINDOW.getSeconds(), TimeUnit.SECONDS);
            return true;
        }

        if (requests >= MAX_REQUESTS) {
            return false;
        }

        redisTemplate.opsForValue().increment(key);
        return true;
    }
}
