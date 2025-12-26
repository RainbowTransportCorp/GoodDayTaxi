package com.gooddaytaxi.dispatch.infrastructure.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${REDIS_HOST:10.2.0.28}")
    private String redisHost;

    @Value("${REDIS_PORT:6379}")
    private int redisPort;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        // 환경 변수로 설정된 Redis host와 port 사용
        config.useSingleServer()
            .setAddress("redis://" + redisHost + ":" + redisPort);

        return Redisson.create(config);
    }
}
