package com.gooddaytaxi.dispatch.infrastructure.client.account;

import com.gooddaytaxi.dispatch.infrastructure.client.account.dto.DriverInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "account-service", url = "${service.account.url}") //로컬 -> 도커 사용시 url 필수
public interface AccountDriverClient {

    @GetMapping("/internal/v1/account/drivers/available")
    DriverInfo getAvailableDrivers(@RequestParam String pickupAddress);
}

