package com.gooddaytaxi.payment.infrastructure.redis;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

//@Component
@RequiredArgsConstructor
public class RedisConnectionTest {
    private final RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void test() {
        redisTemplate.opsForValue().set("redis:test", "ok");
        String value = redisTemplate.opsForValue().get("redis:test");

        System.out.println("Redis value = " + value);
    }
}
