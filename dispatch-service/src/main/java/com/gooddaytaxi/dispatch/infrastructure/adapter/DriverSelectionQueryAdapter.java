package com.gooddaytaxi.dispatch.infrastructure.adapter;

import com.gooddaytaxi.dispatch.application.port.out.query.DriverSelectionQueryPort;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.infrastructure.client.account.AccountDriverClient;
import com.gooddaytaxi.dispatch.infrastructure.client.account.dto.DriverInfo;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DriverSelectionQueryAdapter implements DriverSelectionQueryPort {

    private final AccountDriverClient accountDriverClient;
    private final Random random = new Random();

    @Override
    public UUID selectCandidateDriver(Dispatch dispatch) {

        List<DriverInfo> availableDrivers =
            accountDriverClient.getAvailableDrivers(dispatch.getPickupAddress());

        if (availableDrivers.isEmpty()) {
            throw new IllegalStateException("가용 기사 없음");
        }

        // GIS 없으면 랜덤 선택 or 정책 기반 선택
        DriverInfo candidate =
            availableDrivers.get(random.nextInt(availableDrivers.size()));

        return candidate.driverId();
    }
}

