package com.gooddaytaxi.dispatch.application.service.passenger;

import com.gooddaytaxi.dispatch.application.exception.DispatchNotFoundException;
import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.application.query.DispatchDetailResult;
import com.gooddaytaxi.dispatch.application.query.DispatchSummaryResult;
import com.gooddaytaxi.dispatch.application.usecase.query.PassengerQueryPermissionValidator;
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
public class PassengerDispatchQueryService {

    private final DispatchQueryPort queryPort;
    private final PassengerQueryPermissionValidator permissionValidator;

    /**
     * 승객의 콜 목록 조회
     */
    public List<DispatchSummaryResult> getDispatchList(UUID passengerId, UserRole role) {

        log.info("[PassengerDispatchList] 조회 요청 - passengerId={}", passengerId);

        permissionValidator.validate(role);

        List<Dispatch> dispatches = queryPort.findAllByPassengerId(passengerId);

        log.info("[PassengerDispatchList] 조회 완료 - count={}", dispatches.size());

        return dispatches.stream()
                .map(d -> DispatchSummaryResult.builder()
                        .dispatchId(d.getDispatchId())
                        .pickupAddress(d.getPickupAddress())
                        .destinationAddress(d.getDestinationAddress())
                        .dispatchStatus(d.getDispatchStatus())
                        .createdAt(d.getCreatedAt())
                        .build()
                )
                .toList();
    }

    /**
     * 승객의 콜 상세 정보 조회
     */
    public DispatchDetailResult getDispatchDetail(UUID passengerId,UserRole role, UUID dispatchId) {

        log.info("[PassengerDispatchDetail] 조회 요청 - dispatchId={}", dispatchId);

        permissionValidator.validate(role);

        Dispatch dispatch = queryPort
                .findByIdAndPassengerId(dispatchId, passengerId)
                .orElseThrow(() -> new DispatchNotFoundException(dispatchId));

        log.info("[PassengerDispatchDetail] 조회 완료 - dispatchId={} status={}",
                dispatch.getDispatchId(), dispatch.getDispatchStatus());

        return DispatchDetailResult.builder()
                .dispatchId(dispatch.getDispatchId())
                .passengerId(dispatch.getPassengerId())
                .driverId(dispatch.getDriverId())
                .pickupAddress(dispatch.getPickupAddress())
                .destinationAddress(dispatch.getDestinationAddress())
                .dispatchStatus(dispatch.getDispatchStatus())
                .requestCreatedAt(dispatch.getRequestCreatedAt())
                .assignedAt(dispatch.getAssignedAt())
                .acceptedAt(dispatch.getAcceptedAt())
                .cancelledAt(dispatch.getCancelledAt())
                .timeoutAt(dispatch.getTimeoutAt())
                .createdAt(dispatch.getCreatedAt())
                .updatedAt(dispatch.getUpdatedAt())
                .build();
    }
}

