package com.gooddaytaxi.dispatch.infrastructure.adapter;

import com.gooddaytaxi.dispatch.application.port.out.query.AccountDriverSelectionQueryPort;
import com.gooddaytaxi.dispatch.domain.exception.DriverUnavailableException;
import com.gooddaytaxi.dispatch.infrastructure.client.account.AccountDriverClient;
import com.gooddaytaxi.dispatch.infrastructure.client.account.dto.DriverInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AccountDriverSelectionQueryAdapter implements AccountDriverSelectionQueryPort {

    private final AccountDriverClient feignClient;

    @Override
    public DriverInfo getAvailableDrivers(String pickupAddress) {
        DriverInfo info = feignClient.getAvailableDrivers(pickupAddress);

        if (info.driverIds().isEmpty()) {
            throw new DriverUnavailableException();
        }

        return info;
    }
}
