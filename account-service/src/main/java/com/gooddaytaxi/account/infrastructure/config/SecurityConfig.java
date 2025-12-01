package com.gooddaytaxi.account.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 보안 설정 클래스
 */
@Configuration
public class SecurityConfig {

    /**
     * BCryptPasswordEncoder 빈 등록
     *
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}