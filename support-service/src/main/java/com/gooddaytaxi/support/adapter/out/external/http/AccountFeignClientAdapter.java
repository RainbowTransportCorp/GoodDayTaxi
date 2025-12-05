package com.gooddaytaxi.support.adapter.out.external.http;

import com.gooddaytaxi.support.adapter.out.external.http.dto.UserInfo;
import com.gooddaytaxi.support.application.port.in.account.AccountDomainCommunicationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountFeignClientAdapter implements AccountDomainCommunicationPort {

    private final AccountFeignClient accountFeignClient;

    @Override
    public UserInfo getUserInfo(UUID userId) {
        return accountFeignClient.getUserInfo(userId);
    }
}