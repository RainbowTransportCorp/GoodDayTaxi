package com.gooddaytaxi.support.adapter.out.external.http;

import com.gooddaytaxi.support.application.port.in.account.AccountDomainCommunicationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

/* Account Domain과 통신하기 위한 Feign Client
*
*/

@Slf4j
@RequiredArgsConstructor
@FeignClient(name = "account-service")
public class AccountFeignClient implements AccountDomainCommunicationPort {

    @Override
    @GetMapping("/internal/users/{driverId}")
    public String getExternalInfo(@PathVariable("driverId") UUID driverId) {
        return "personal-id-for-slack";
    }
}
