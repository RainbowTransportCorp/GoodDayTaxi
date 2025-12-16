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
import com.gooddaytaxi.dispatch.application.usecase.accept.DispatchAcceptPermissionValidator;
import com.gooddaytaxi.dispatch.application.usecase.accept.DispatchAcceptResult;
import com.gooddaytaxi.dispatch.domain.exception.dispatch.DispatchAlreadyAssignedByOthersException;
import com.gooddaytaxi.dispatch.application.exception.auth.DispatchNotAssignedDriverException;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchAssignmentLog;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
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

    private final DispatchAcceptPermissionValidator permissionValidator;

    public DispatchAcceptResult accept(DispatchAcceptCommand command) throws InterruptedException {

        log.info("[Accept] 요청 수신 - driverId={} dispatchId={}",
                command.getDriverId(), command.getDispatchId());

        String lockKey = command.getDispatchId().toString();

        if (!lockManager.tryLock(lockKey, command.getDriverId())) {
            log.warn("[Accept] 락 획득 실패 - 이미 다른 기사 처리 중, dispatchId={}", command.getDispatchId());
            throw new DispatchAlreadyAssignedByOthersException();
        }

        try {
            // 1. Dispatch 조회
            Dispatch dispatch = queryPort.findById(command.getDispatchId());
            log.debug("[Accept] Dispatch 조회 완료 - status={}", dispatch.getDispatchStatus());

            // 2. 역할 검증
            permissionValidator.validate(command.getRole());
            log.debug("[Accept] 역할 검증 통과 - role={}", command.getRole());

            // 3. 후보 기사 여부 검증
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

            log.debug("[Accept] 후보 기사 확인 완료 - driverId={} dispatchId={}",
                    command.getDriverId(), command.getDispatchId());

            DispatchStatus before = dispatch.getDispatchStatus();

            // 4. 상태 전이
            dispatch.assignedTo(command.getDriverId());
            dispatch.accept();
            logEntry.accept();

            log.info("[Accept] 상태 전이 완료 - dispatchId={} driverId={}",
                    dispatch.getDispatchId(), command.getDriverId());

            // 5. 상태 저장
            assignmentLogService.save(logEntry);
            commandPort.save(dispatch);

            // 6. 이벤트 발행
            acceptedEventPort.publishAccepted(
                    DispatchAcceptedPayload.from(dispatch, command.getDriverId())
            );
            tripEventPort.publishTripCreateRequest(
                    TripCreateRequestPayload.from(dispatch)
            );

            log.info("[Accept] 이벤트 발행 완료 - dispatchId={}", dispatch.getDispatchId());

            // 7. 히스토리 (실패해도 흐름 유지)
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

        } finally {
            lockManager.unlock(lockKey);
            log.debug("[Accept] 락 해제 - dispatchId={}", command.getDispatchId());
        }
    }
}
