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

    private final DriverQueryPermissionValidator driverQueryPermissionValidator;

    /**
     * 기사의 배차 대기 콜 목록을 조회하는 서비스
     * @param driverId 특정 기사(관리자 외에는 기사 본인)의 UUID
     * @param role driver UserRole
     * @return 출발/도착지와 상태값 등이 포함된 콜 목록 Result
     */
    public List<DispatchPendingListResult> getDriverPendingDispatch(UUID driverId, UserRole role) {

        log.debug("[DriverPending] 조회 요청 - driverId={}", driverId);
        driverQueryPermissionValidator.validate(role);

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
