package com.gooddaytaxi.dispatch.application.service;

import com.gooddaytaxi.dispatch.application.event.payload.TripCreateRequestPayload;
import com.gooddaytaxi.dispatch.application.query.DispatchPendingListResult;
import com.gooddaytaxi.dispatch.application.usecase.accept.DispatchAcceptCommand;
import com.gooddaytaxi.dispatch.application.usecase.accept.DispatchAcceptResult;
import com.gooddaytaxi.dispatch.application.usecase.reject.DispatchRejectCommand;
import com.gooddaytaxi.dispatch.application.event.payload.DispatchAcceptedPayload;
import com.gooddaytaxi.dispatch.application.event.payload.DispatchRejectedPayload;
import com.gooddaytaxi.dispatch.application.port.out.command.*;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.application.usecase.reject.DispatchRejectResult;
import com.gooddaytaxi.dispatch.application.exception.auth.DispatchDriverPermissionValidator;
import com.gooddaytaxi.dispatch.domain.exception.DispatchAlreadyAssignedByOthersException;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchAssignmentLog;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchHistory;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchDomainEventType;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import com.gooddaytaxi.dispatch.domain.repository.DispatchAssignmentLogRepository;
import com.gooddaytaxi.dispatch.infrastructure.redis.DispatchLockManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DriverDispatchService {

    private final DispatchCommandPort dispatchCommandPort;
    private final DispatchHistoryCommandPort dispatchHistoryCommandPort;

    private final DispatchQueryPort dispatchQueryPort;

    private final DispatchAssignmentLogRepository dispatchAssignmentLogRepository;

    private final DispatchAcceptedCommandPort dispatchAcceptedCommandPort;
    private final DispatchRejectedCommandPort dispatchRejectedCommandPort;
    private final TripCreateRequestCommandPort tripCreateRequestCommandPort;

    private final DispatchDriverPermissionValidator dispatchDriverPermissionValidator;

    private final DispatchLockManager dispatchLockManager;

    /**
     * 배차 대기 (ASSIGNING) 상태 콜 조회 (기사)
     *
     * @param userID
     * @return
     */
    @Transactional(readOnly = true)
    public List<DispatchPendingListResult> getDriverPendingDispatch(UUID userID) {

        log.info("[Driver Pending Dispatch] 조회 시작 - driverId={}", userID);

        List<Dispatch> dispatches = dispatchQueryPort.findByStatus(DispatchStatus.ASSIGNING);

        log.info("[Driver Pending Dispatch] 조회 완료 - driverId={}, count={}",
                userID, dispatches.size());

        dispatches.stream()
                .limit(5)
                .forEach(d -> log.debug("[PendingDispatch] id={}", d.getDispatchId()));

        return dispatches.stream()
                .map(dispatch -> DispatchPendingListResult.builder()
                        .dispatchId(dispatch.getDispatchId())
                        .pickupAddress(dispatch.getPickupAddress())
                        .destinationAddress(dispatch.getDestinationAddress())
                        .dispatchStatus(dispatch.getDispatchStatus())
                        .requestCreatedAt(dispatch.getRequestCreatedAt())
                        .build()
                )
                .toList();
    }


    /**
     * 콜 수락
     *
     * @param command
     * @return
     */
    public DispatchAcceptResult accept(DispatchAcceptCommand command) throws InterruptedException {

        dispatchDriverPermissionValidator.validate(command.getRole());

        log.info("[Accept] 콜 수락 요청 수신 - driverId={}, dispatchId={}",
                command.getDriverId(), command.getDispatchId());

        String lockKey = command.getDispatchId().toString();

        if (!dispatchLockManager.tryLock(lockKey, command.getDriverId())) {
            log.warn("[Accept] 락 획득 실패 - 이미 다른 기사가 먼저 수락했을 가능성 있음 - dispatchId={}",
                    command.getDispatchId());
            throw new DispatchAlreadyAssignedByOthersException();
        }

        try {
            Dispatch dispatch = dispatchQueryPort.findById(command.getDispatchId());

            log.debug("[Accept] Dispatch 조회 - id={}, status={}",
                    dispatch.getDispatchId(), dispatch.getDispatchStatus());

            DispatchStatus from = dispatch.getDispatchStatus();

            // ================================================
            // 1) AssignmentLog 조회 (내부에서 상태 검증)
            // ================================================
            DispatchAssignmentLog assignmentLog =
                    dispatchAssignmentLogRepository.findLatest(
                            command.getDispatchId(),
                            command.getDriverId()
                    ).orElseThrow(() -> new IllegalStateException("AssignmentLog 없음"));

            // ================================================
            // 2) ASSIGNING → ASSIGNED
            // ================================================
            log.info("[Accept] 배차 확정 진행 (ASSIGNING → ASSIGNED) - dispatchId={}, driverId={}",
                    dispatch.getDispatchId(), command.getDriverId());

            dispatch.assignedTo(command.getDriverId());

            // ================================================
            // 3) ASSIGNED → ACCEPTED
            // ================================================
            dispatch.accept();

            // ================================================
            // 4) AssignmentLog SENT → ACCEPTED
            //    (여기서 SENT인지 자동 검증됨!)
            // ================================================
            assignmentLog.accept();
            dispatchAssignmentLogRepository.save(assignmentLog);

            log.info("[Accept] AssignmentLog 업데이트 완료 - dispatchId={}, newStatus=ACCEPTED",
                    dispatch.getDispatchId());

            // ================================================
            // 5) Dispatch 저장 + 히스토리
            // ================================================
            dispatchCommandPort.save(dispatch);

            dispatchHistoryCommandPort.save(
                    DispatchHistory.recordStatusChange(
                            dispatch.getDispatchId(),
                            DispatchDomainEventType.ACCEPTED.name(),
                            from,
                            dispatch.getDispatchStatus(),
                            ChangedBy.DRIVER,
                            null
                    )
            );

            // ================================================
            // 6) 이벤트 발행
            // ================================================
            dispatchAcceptedCommandPort.publishAccepted(
                    DispatchAcceptedPayload.from(dispatch, command.getDriverId())
            );

            tripCreateRequestCommandPort.publishTripCreateRequest(
                    TripCreateRequestPayload.from(dispatch)
            );

            // ================================================
            // 7) 응답 생성
            // ================================================
            return DispatchAcceptResult.builder()
                    .dispatchId(dispatch.getDispatchId())
                    .driverId(command.getDriverId())
                    .dispatchStatus(dispatch.getDispatchStatus())
                    .acceptedAt(dispatch.getAcceptedAt())
                    .build();

        } finally {
            dispatchLockManager.unlock(lockKey);
        }
    }

    /**
     * 콜 거절
     *
     * @param command
     * @return
     */
    public DispatchRejectResult reject(DispatchRejectCommand command) {

        dispatchDriverPermissionValidator.validate(command.getRole());

        log.info("[Reject] 콜 거절 요청 수신 - driverId={}, dispatchId={}",
                command.getDriverId(), command.getDispatchId());

        Dispatch dispatch = dispatchQueryPort.findById(command.getDispatchId());

        log.debug("[Reject] 조회된 Dispatch 엔티티 - id={}, status={}",
                dispatch.getDispatchId(), dispatch.getDispatchStatus());

        DispatchStatus from = dispatch.getDispatchStatus();

        // === AssignmentLog 상태 전이 ===
        DispatchAssignmentLog assignmentLog =
                dispatchAssignmentLogRepository.findLatest(
                        command.getDispatchId(),
                        command.getDriverId()
                ).orElseThrow(
                        () -> new IllegalStateException("AssignmentLog 업데이트 실패")
                );

        assignmentLog.reject();   // SENT → REJECTED
        dispatchAssignmentLogRepository.save(assignmentLog);

        // === Dispatch 상태 전이 ===
        dispatch.rejectedByDriver(command.getDriverId());

        log.info("[Reject] Dispatch 상태 전이 완료 - id={}, newStatus={}",
                dispatch.getDispatchId(), dispatch.getDispatchStatus());

        DispatchStatus to = dispatch.getDispatchStatus();
        dispatchCommandPort.save(dispatch);

        // === 히스토리 기록 ===
        dispatchHistoryCommandPort.save(
                DispatchHistory.recordStatusChange(
                        dispatch.getDispatchId(),
                        DispatchDomainEventType.REJECTED.name(),
                        from,
                        to,
                        ChangedBy.DRIVER,
                        null
                )
        );

        // === 이벤트 발행 ===
        dispatchRejectedCommandPort.publishRejected(
                DispatchRejectedPayload.from(
                        dispatch.getDispatchId(),
                        command.getDriverId(),
                        LocalDateTime.now()
                )
        );

        DispatchRejectResult result = DispatchRejectResult.builder()
                .dispatchId(command.getDispatchId())
                .driverId(command.getDriverId())
                .dispatchStatus(dispatch.getDispatchStatus())
                .rejectedAt(LocalDateTime.now())
                .build();

        log.info("[Reject] 콜 거절 처리 완료 - dispatchId={}, rejectedAt={}",
                result.getDispatchId(), result.getRejectedAt());

        return result;
    }

}
