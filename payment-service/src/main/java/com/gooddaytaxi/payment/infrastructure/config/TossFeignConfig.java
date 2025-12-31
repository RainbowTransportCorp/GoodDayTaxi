package com.gooddaytaxi.payment.infrastructure.config;

import feign.Request;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class TossFeignConfig {

    @Value("${toss.secret-key}")
    private String secretKey;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            String encodedKey = java.util.Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
            requestTemplate.header("Authorization", "Basic " + encodedKey);
            requestTemplate.header("Content-Type", "application/json");
        };
    }

    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(3, TimeUnit.SECONDS, //TCP 연결에 최대 3초만 대기-> 안되면 실패 처리
                5, TimeUnit.SECONDS,   //응답 대기 시간은 최대 5초-> 이내에 응답 없으면 실패 처리
                true);  //Http 301/302 응답시 클라이언트가 자동으로 리다이렉트를 따라가도록 설정
    }
}
