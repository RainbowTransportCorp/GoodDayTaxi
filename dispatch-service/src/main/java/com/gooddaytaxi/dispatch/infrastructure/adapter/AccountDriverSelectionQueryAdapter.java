package com.gooddaytaxi.dispatch.infrastructure.adapter;

import com.gooddaytaxi.dispatch.application.port.out.query.AccountDriverSelectionQueryPort;
import com.gooddaytaxi.dispatch.application.exception.DriverUnavailableException;
import com.gooddaytaxi.dispatch.infrastructure.client.account.AccountDriverClient;
import com.gooddaytaxi.dispatch.infrastructure.client.account.dto.DriverInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class AccountDriverSelectionQueryAdapter implements AccountDriverSelectionQueryPort {

    private final AccountDriverClient feignClient;

    @Override
    public DriverInfo getAvailableDrivers(String pickupAddress) {
        DriverInfo info = feignClient.getAvailableDrivers(pickupAddress);
        log.info("[Account] available drivers count={}, pickup={}", info.totalCount(), pickupAddress);
        log.info("[Account] drivers={}", info);

        if (info.driverIds().isEmpty()) {
            throw new DriverUnavailableException();
        }

        return info;
    }
}
