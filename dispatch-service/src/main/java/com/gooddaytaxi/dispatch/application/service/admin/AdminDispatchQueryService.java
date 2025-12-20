package com.gooddaytaxi.dispatch.application.service.admin;

import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.application.usecase.admin.AdminDispatchDetailResult;
import com.gooddaytaxi.dispatch.application.usecase.admin.AdminDispatchListResult;
import com.gooddaytaxi.dispatch.application.usecase.timeout.AdminPermissionValidator;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDispatchQueryService {

    private final DispatchQueryPort queryPort;

    private final AdminPermissionValidator adminPermissionValidator;

    /**
     * 일반 관리자 및 최고 관리자가 배차를 전체 조회
     * @param role 요청자의 권한
     * @param status 조회하고 싶은 배차의 상태
     * @return 상태값에 따른 배차 목록
     */
    public List<AdminDispatchListResult> getDispatches(UserRole role, DispatchStatus status) {

        adminPermissionValidator.validateAdminRead(role);

        List<Dispatch> dispatches =
                (status == null)
                        ? queryPort.findAll()
                        : queryPort.findByStatus(status);
        return dispatches.stream()
                .map(dispatch -> AdminDispatchListResult.builder()
                        .dispatchId(dispatch.getDispatchId())
                        .passengerId(dispatch.getPassengerId())
                        .driverId(dispatch.getDriverId())
                        .status(dispatch.getDispatchStatus())
                        .reassignCount(dispatch.getReassignAttemptCount())
                        .requestedAt(dispatch.getRequestCreatedAt())
                        .updatedAt(dispatch.getUpdatedAt())
                        .build()
                )
                .toList();
    }

    /**
     * 관리자 및 최고 관리자가 특정 배차의 상세정보를 조회
     * @param role 요청자의 권한
     * @param dispatchId 요청한 특정 배차의 식별자
     * @return 요청된 배차의 상세정보
     */
    public AdminDispatchDetailResult getDispatchDetail(
            UserRole role,
            UUID dispatchId
    ) {
        adminPermissionValidator.validateAdminRead(role);

        Dispatch dispatch = queryPort.findById(dispatchId);

        return AdminDispatchDetailResult.builder()
                .dispatchId(dispatch.getDispatchId())
                .passengerId(dispatch.getPassengerId())
                .driverId(dispatch.getDriverId())
                .status(dispatch.getDispatchStatus())
                .pickupAddress(dispatch.getPickupAddress())
                .destinationAddress(dispatch.getDestinationAddress())
                .reassignCount(dispatch.getReassignAttemptCount())
                .assignedAt(dispatch.getAssignedAt())
                .acceptedAt(dispatch.getAcceptedAt())
                .timeoutAt(dispatch.getTimeoutAt())
                .build();
    }
}
