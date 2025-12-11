package com.gooddaytaxi.support.adapter.out.internal.account;

import com.gooddaytaxi.support.adapter.out.internal.account.dto.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class AccountFeignClientFallback implements AccountFeignClient {
    @Override
    public UserInfo getUserInfo(String userId) {
        log.error("❌ AccountFeignClient fallback triggered userId={}", userId);
        return null; // 실패 시 기본 값 반환
    }
}

