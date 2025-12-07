package com.gooddaytaxi.dispatch.infrastructure.client.account;

import com.gooddaytaxi.dispatch.infrastructure.client.account.dto.DriverInfo;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "account-service", url = "${service.account.url}")
public interface AccountDriverClient {

    @GetMapping("/internal/v1/drivers/available")
    List<DriverInfo> getAvailableDrivers(@RequestParam String pickupAddress);
}

