package com.gooddaytaxi.dispatch.application.service.passenger;

import com.gooddaytaxi.dispatch.application.exception.DispatchNotFoundException;
import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.application.query.DispatchDetailResult;
import com.gooddaytaxi.dispatch.application.query.DispatchSummaryResult;
import com.gooddaytaxi.dispatch.application.usecase.query.PassengerQueryPermissionValidator;
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
public class PassengerDispatchQueryService {

    private final DispatchQueryPort queryPort;
    private final PassengerQueryPermissionValidator passengerQueryPermissionValidator;


    /**
     * 승객이 호출한 배차(콜)의 목록 조회
     * @param passengerId 요청 승객의 식별자
     * @param role 요청 승객의 권한
     * @return 요청자가 호출했던 배차의 리스트
     */
    public List<DispatchSummaryResult> getDispatchList(UUID passengerId, UserRole role) {

        log.info("[PassengerDispatchList] 조회 요청 - passengerId={}", passengerId);

        passengerQueryPermissionValidator.validate(role);

        List<Dispatch> dispatches = queryPort.findAllByPassengerId(passengerId);

        log.info("[PassengerDispatchList] 조회 완료 - count={}", dispatches.size());

        return dispatches.stream()
                .filter(d -> d.getDispatchStatus() != DispatchStatus.TIMEOUT) //Service testCode를 위해 필터 추가
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
     * 특정 배차에 대한 상세 조회
     * @param passengerId 요청자의 식별자
     * @param role 요청자의 권한
     * @param dispatchId 조회를 요청한 특정 배차의 식별자
     * @return 특정 배차에 대한 상세 값
     */
    public DispatchDetailResult getDispatchDetail(UUID passengerId,UserRole role, UUID dispatchId) {

        log.info("[PassengerDispatchDetail] 조회 요청 - dispatchId={}", dispatchId);

        passengerQueryPermissionValidator.validate(role);

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
                .canceledAt(dispatch.getCanceledAt())
                .timeoutAt(dispatch.getTimeoutAt())
                .createdAt(dispatch.getCreatedAt())
                .updatedAt(dispatch.getUpdatedAt())
                .build();
    }
}

