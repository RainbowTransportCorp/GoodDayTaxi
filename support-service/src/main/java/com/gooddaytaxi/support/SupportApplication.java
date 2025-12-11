package com.gooddaytaxi.support;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = {
        "com.gooddaytaxi.support.adapter.out.internal.account",
        "com.gooddaytaxi.support.adapter.out.external.slack"
})
@SpringBootApplication
public class SupportApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupportApplication.class, args);
    }

}
