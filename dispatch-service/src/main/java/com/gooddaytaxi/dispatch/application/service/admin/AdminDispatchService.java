package com.gooddaytaxi.dispatch.application.service.admin;

import com.gooddaytaxi.dispatch.application.event.payload.DispatchForceTimeoutPayload;
import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchForceTimeoutCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.application.service.dispatch.DispatchHistoryService;
import com.gooddaytaxi.dispatch.application.usecase.timeout.AdminForceTimeoutCommand;
import com.gooddaytaxi.dispatch.application.usecase.timeout.AdminForceTimeoutResult;
import com.gooddaytaxi.dispatch.application.usecase.timeout.AdminPermissionValidator;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import com.gooddaytaxi.dispatch.domain.model.enums.HistoryEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminDispatchService {

    private final DispatchQueryPort queryPort;
    private final DispatchCommandPort commandPort;
    private final DispatchHistoryService historyService;

    private final DispatchForceTimeoutCommandPort forceTimeoutCommandPort;

    private final AdminPermissionValidator adminPermissionValidator;

    /**
     * 관리자 강제 TIMEOUT 처리
     */
    public AdminForceTimeoutResult forceTimeout(
        UserRole role,
        UUID dispatchId,
        AdminForceTimeoutCommand command
    ) {

        adminPermissionValidator.validateMasterWrite(role);

        Dispatch dispatch = queryPort.findById(dispatchId);

        DispatchStatus before = dispatch.getDispatchStatus();

        // 도메인 상태 전이
        dispatch.forceTimeout(); // REQUESTED / ASSIGNING / ASSIGNED / ACCEPTED / TRIP_REQUEST 모두 허용

        commandPort.save(dispatch);

        // 히스토리
        historyService.saveStatusChange(
            dispatchId,
            HistoryEventType.TIMEOUT,
            before,
            dispatch.getDispatchStatus(),
            ChangedBy.MASTER_ADMIN,
            command.getReason()
        );

        // ✅ 한 번만 찍어서 이벤트/응답 결과에 동일하게 사용
        LocalDateTime forceTimeoutAt = LocalDateTime.now();

        // ✅ 기사 알림 목적이라 driverId 없으면 발행 스킵
        UUID driverId = dispatch.getDriverId(); // (필드명이 다르면 여기에 맞춰 변경)
        if (driverId != null) {
            forceTimeoutCommandPort.publish(
                DispatchForceTimeoutPayload.adminForce(
                    dispatch.getDispatchId(),
                    driverId,
                    before,                   // previousStatus
                    command.getAdminId(),     // notifierId = MASTER_ADMIN
                    command.getReason(),
                    forceTimeoutAt
                )
            );
        } else {
            log.info("[Admin][ForceTimeout][SkipEvent] dispatchId={} before={} reason={} (driverId is null)",
                dispatchId, before, command.getReason());
        }

        log.info("[Admin][ForceTimeout] dispatchId={} before={} after={}",
            dispatchId, before, dispatch.getDispatchStatus());

        return AdminForceTimeoutResult.builder()
            .dispatchId(dispatch.getDispatchId())
            .status(dispatch.getDispatchStatus())
            .timeoutAt(forceTimeoutAt)
            .build();
    }
}
