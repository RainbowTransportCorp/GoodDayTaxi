package com.gooddaytaxi.dispatch.application.service.driver;

import com.gooddaytaxi.dispatch.application.event.payload.DispatchRejectedPayload;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchRejectedCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.application.service.assignmentLog.AssignmentLogLifecycleService;
import com.gooddaytaxi.dispatch.application.service.dispatch.DispatchHistoryService;
import com.gooddaytaxi.dispatch.application.usecase.reject.DispatchRejectCommand;
import com.gooddaytaxi.dispatch.application.usecase.reject.DispatchRejectPermissionValidator;
import com.gooddaytaxi.dispatch.application.usecase.reject.DispatchRejectResult;
import com.gooddaytaxi.dispatch.domain.exception.DispatchNotAssignedDriverException;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchAssignmentLog;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchDomainEventType;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import com.gooddaytaxi.dispatch.domain.model.enums.HistoryEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchRejectService {

    private final DispatchQueryPort queryPort;
    private final DispatchCommandPort commandPort;

    private final AssignmentLogLifecycleService assignmentLogService;
    private final DispatchHistoryService historyService;

    private final DispatchRejectedCommandPort eventPort;

    private final DispatchRejectPermissionValidator permissionValidator;

    public DispatchRejectResult reject(DispatchRejectCommand command) {

        log.info("[Reject] 요청 수신 - driverId={} dispatchId={}",
                command.getDriverId(), command.getDispatchId());

        Dispatch dispatch = queryPort.findById(command.getDispatchId());

        // 1. 역할 검증
        permissionValidator.validate(command.getRole());

        // 2. 후보 기사 여부 검증 (핵심)
        DispatchAssignmentLog logEntry =
                assignmentLogService.findLatest(
                        command.getDispatchId(),
                        command.getDriverId()
                );

        if (logEntry == null) {
            log.warn("[Reject] 후보 기사 아님 - driverId={} dispatchId={}",
                    command.getDriverId(), command.getDispatchId());
            throw new DispatchNotAssignedDriverException();
        }

        DispatchStatus before = dispatch.getDispatchStatus();

        // 3. 도메인 처리
        domainService.processReject(dispatch, logEntry, command.getDriverId());

        // 4. 상태 저장
        assignmentLogService.save(logEntry);
        commandPort.save(dispatch);

        // 5. 이벤트 발행
        eventPort.publishRejected(
                DispatchRejectedPayload.from(dispatch));

        // 6. 히스토리 (실패해도 흐름 유지)
        try {
            historyService.saveStatusChange(
                    dispatch.getDispatchId(),
                    HistoryEventType.DRIVER_REJECTED,
                    before,
                    dispatch.getDispatchStatus(),
                    ChangedBy.DRIVER
            );
        } catch (Exception e) {
            log.error("[Reject] 히스토리 기록 실패 - dispatchId={} err={}",
                    dispatch.getDispatchId(), e.getMessage());
        }

        log.info("[Reject] 완료 - dispatchId={}", dispatch.getDispatchId());

        return DispatchRejectResult.builder()
                .dispatchId(command.getDispatchId())
                .driverId(command.getDriverId())
                .dispatchStatus(dispatch.getDispatchStatus())
                .rejectedAt(LocalDateTime.now())
                .build();
    }
}
