package com.gooddaytaxi.dispatch.application.service.driver;

import com.gooddaytaxi.dispatch.application.event.payload.DispatchAcceptedPayload;
import com.gooddaytaxi.dispatch.application.event.payload.TripCreateRequestPayload;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchAcceptedCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.TripCreateRequestCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.application.service.assignmentLog.AssignmentLogLifecycleService;
import com.gooddaytaxi.dispatch.application.service.dispatch.DispatchHistoryService;
import com.gooddaytaxi.dispatch.application.usecase.accept.DispatchAcceptCommand;
import com.gooddaytaxi.dispatch.application.usecase.accept.DispatchAcceptResult;
import com.gooddaytaxi.dispatch.domain.exception.DispatchAlreadyAssignedByOthersException;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchAssignmentLog;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchDomainEventType;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import com.gooddaytaxi.dispatch.domain.model.enums.HistoryEventType;
import com.gooddaytaxi.dispatch.infrastructure.redis.DispatchLockManager;
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
    private final DispatchHistoryService historyService;

    private final DispatchAcceptedCommandPort acceptedEventPort;
    private final TripCreateRequestCommandPort tripEventPort;

    private final DispatchLockManager lockManager;

    public DispatchAcceptResult accept(DispatchAcceptCommand command) throws InterruptedException {

        log.info("[Accept] 요청 수신 - driverId={} dispatchId={}",
                command.getDriverId(), command.getDispatchId());

        String lockKey = command.getDispatchId().toString();
        if (!lockManager.tryLock(lockKey, command.getDriverId())) {
            log.warn("[Accept] 락 획득 실패 - 다른 기사가 먼저 처리");
            throw new DispatchAlreadyAssignedByOthersException();
        }

        try {
            Dispatch dispatch = queryPort.findById(command.getDispatchId());
            DispatchStatus before = dispatch.getDispatchStatus();

            DispatchAssignmentLog logEntry =
                    assignmentLogService.findLatest(command.getDispatchId(), command.getDriverId());

            // 엔티티 상태전이
            dispatch.assignedTo(command.getDriverId());
            dispatch.accept();
            logEntry.accept();

            // 상태 저장
            assignmentLogService.save(logEntry);
            commandPort.save(dispatch);

            // 이벤트 발행 (핵심)
            acceptedEventPort.publishAccepted(
                    DispatchAcceptedPayload.from(dispatch, command.getDriverId())
            );

            tripEventPort.publishTripCreateRequest(
                    TripCreateRequestPayload.from(dispatch)
            );

            // 히스토리는 실패해도 흐름 유지
            try {
                historyService.saveStatusChange(
                        dispatch.getDispatchId(),
                        HistoryEventType.STATUS_CHANGED,
                        before,
                        dispatch.getDispatchStatus(),
                        ChangedBy.DRIVER
                );
            } catch (Exception e) {
                log.error("[Accept] 히스토리 기록 실패 - dispatchId={} err={}",
                        dispatch.getDispatchId(), e.getMessage());
            }

            log.info("[Accept] 완료 - dispatchId={}", dispatch.getDispatchId());

            return DispatchAcceptResult.builder()
                    .dispatchId(dispatch.getDispatchId())
                    .driverId(command.getDriverId())
                    .dispatchStatus(dispatch.getDispatchStatus())
                    .acceptedAt(dispatch.getAcceptedAt())
                    .build();

        } finally {
            lockManager.unlock(lockKey);
        }
    }
}
