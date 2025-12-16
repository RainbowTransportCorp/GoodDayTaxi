package com.gooddaytaxi.dispatch.application.service.driver;

import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchAssignmentLogQueryPort;
import com.gooddaytaxi.dispatch.application.query.DispatchPendingListResult;
import com.gooddaytaxi.dispatch.application.usecase.query.DriverQueryPermissionValidator;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
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

    private final DispatchAssignmentLogQueryPort dispatchAssignmentLogQueryPort;

    private final DriverQueryPermissionValidator permissionValidator;

    /**
     * 기사 대기중 배차(ASSIGNING) 조회
     */
    public List<DispatchPendingListResult> getDriverPendingDispatch(UUID driverId, UserRole role) {

        log.debug("[DriverPending] 조회 요청 - driverId={}", driverId);

        permissionValidator.validate(role);

        List<Dispatch> dispatches =
                dispatchAssignmentLogQueryPort.findAssigningByCandidateDriver(driverId);

        log.debug("[DriverPending] 조회 완료 - driverId={}, count={}",
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
