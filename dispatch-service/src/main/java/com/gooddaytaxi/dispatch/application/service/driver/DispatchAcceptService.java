package com.gooddaytaxi.dispatch.application.service.driver;

import com.gooddaytaxi.dispatch.application.event.payload.DispatchAcceptedPayload;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchAcceptedCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.application.service.assignmentLog.AssignmentLogLifecycleService;
import com.gooddaytaxi.dispatch.application.service.dispatch.DispatchHistoryService;
import com.gooddaytaxi.dispatch.application.service.dispatch.DispatchTripRequestService;
import com.gooddaytaxi.dispatch.application.usecase.accept.DispatchAcceptCommand;
import com.gooddaytaxi.dispatch.application.usecase.accept.DispatchAcceptPermissionValidator;
import com.gooddaytaxi.dispatch.application.usecase.accept.DispatchAcceptResult;
import com.gooddaytaxi.dispatch.application.exception.auth.DispatchNotAssignedDriverException;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchAssignmentLog;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import com.gooddaytaxi.dispatch.domain.model.enums.HistoryEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchAcceptService {

    private final DispatchQueryPort queryPort;
    private final DispatchCommandPort commandPort;

    private final AssignmentLogLifecycleService assignmentLogService;
    private final DispatchTripRequestService dispatchTripRequestService;
    private final DispatchHistoryService historyService;

    private final DispatchAcceptedCommandPort acceptedEventPort;
    private final DispatchAcceptPermissionValidator dispatchAcceptPermissionValidator;

    /**
     * 배차 요청 수락 서비스 (락 미적용 – Baseline 테스트용)
     */
    public DispatchAcceptResult accept(DispatchAcceptCommand command) {

        log.info("[Accept] 요청 수신 - driverId={} dispatchId={}",
            command.getDriverId(), command.getDispatchId());

        Dispatch dispatch = queryPort.findById(command.getDispatchId());
        log.debug("[Accept] Dispatch 조회 완료 - status={}", dispatch.getDispatchStatus());

        dispatchAcceptPermissionValidator.validate(command.getRole());
        log.debug("[Accept] 역할 검증 통과 - role={}", command.getRole());

        DispatchAssignmentLog logEntry =
            assignmentLogService.findLatest(
                command.getDispatchId(),
                command.getDriverId()
            );

        if (logEntry == null) {
            log.warn("[Accept] 후보 기사 아님 - driverId={} dispatchId={}",
                command.getDriverId(), command.getDispatchId());
            throw new DispatchNotAssignedDriverException();
        }

        DispatchStatus before = dispatch.getDispatchStatus();

        // 🚨 동시성 보호 없음
        dispatch.assignedTo(command.getDriverId());
        dispatch.accept();
        logEntry.accept();

        log.info("[Accept] 상태 전이 완료 - dispatchId={} driverId={}",
            dispatch.getDispatchId(), command.getDriverId());

        assignmentLogService.save(logEntry);
        commandPort.save(dispatch);

        acceptedEventPort.publishAccepted(
            DispatchAcceptedPayload.from(dispatch, command.getDriverId())
        );

        dispatchTripRequestService.requestTrip(dispatch.getDispatchId());

        try {
            historyService.saveStatusChange(
                dispatch.getDispatchId(),
                HistoryEventType.STATUS_CHANGED,
                before,
                dispatch.getDispatchStatus(),
                ChangedBy.DRIVER,
                "기사로부터 배차 수락"
            );
        } catch (Exception e) {
            log.error("[Accept] 히스토리 기록 실패 - dispatchId={} err={}",
                dispatch.getDispatchId(), e.getMessage());
        }

        log.info("[Accept] 완료 - dispatchId={} status={}",
            dispatch.getDispatchId(), dispatch.getDispatchStatus());

        return DispatchAcceptResult.builder()
            .dispatchId(dispatch.getDispatchId())
            .driverId(command.getDriverId())
            .dispatchStatus(dispatch.getDispatchStatus())
            .acceptedAt(dispatch.getAcceptedAt())
            .build();
    }
}
