package com.gooddaytaxi.payment.application.port.out.redis;

import java.time.Duration;

public interface RedisPort {
    boolean setIfAbsent(String key, String value, Duration ttl);
    void set(String key, String value, Duration ttl);
    String get(String key);
    boolean delete(String key);
}
