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

    /**
     * 출발지 기준으로 배차 가능한 기사 목록을 조회한다.
     *
     * @param pickupAddress 승객의 출발지 주소
     * @return Account 서비스에서 필터링된 배차 가능 기사 정보
     */
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
