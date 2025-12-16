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
