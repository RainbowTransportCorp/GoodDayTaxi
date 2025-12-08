package com.gooddaytaxi.support.adapter.out.internal.account;

import com.gooddaytaxi.support.adapter.out.internal.account.dto.UserInfo;
import com.gooddaytaxi.support.application.port.out.internal.account.AccountDomainCommunicationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** 메시지 전송 알림을 위한 Slack Adapter - FeignClient
 *
 */
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