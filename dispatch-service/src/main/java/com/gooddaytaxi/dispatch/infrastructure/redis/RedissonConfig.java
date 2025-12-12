package com.gooddaytaxi.dispatch.infrastructure.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        // 로컬 Redis 기준 (docker-compose)
        config.useSingleServer()
                .setAddress("redis://localhost:6379");

        return Redisson.create(config);
    }
}
