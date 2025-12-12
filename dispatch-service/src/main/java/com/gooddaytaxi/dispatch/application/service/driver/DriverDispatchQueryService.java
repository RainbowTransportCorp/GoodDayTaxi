package com.gooddaytaxi.dispatch.application.service.driver;

import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.application.query.DispatchPendingListResult;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverDispatchQueryService {

    private final DispatchQueryPort queryPort;

    /**
     * 기사 대기중 배차(ASSIGNING) 조회
     */
    public List<DispatchPendingListResult> getDriverPendingDispatch(UUID driverId) {

        log.info("[DriverPending] 조회 요청 - driverId={}", driverId);

        List<Dispatch> dispatches = queryPort.findByStatus(DispatchStatus.ASSIGNING);

        log.info("[DriverPending] 조회 완료 - driverId={}, count={}",
                driverId, dispatches.size());

        return dispatches.stream()
                .map(d -> DispatchPendingListResult.builder()
                        .dispatchId(d.getDispatchId())
                        .pickupAddress(d.getPickupAddress())
                        .destinationAddress(d.getDestinationAddress())
                        .dispatchStatus(d.getDispatchStatus())
                        .requestCreatedAt(d.getRequestCreatedAt())
                        .build()
                )
                .toList();
    }
}
