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
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchAssignmentLog;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchHistory;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchDomainEventType;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
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
    private final DispatchAssignmentCommandPort dispatchAssignmentCommandPort;

    private final DispatchQueryPort dispatchQueryPort;

    private final DispatchAcceptedCommandPort dispatchAcceptedCommandPort;
    private final DispatchRejectedCommandPort dispatchRejectedCommandPort;
    private final TripCreateRequestCommandPort tripCreateRequestCommandPort;

    private final DispatchDriverPermissionValidator dispatchDriverPermissionValidator;


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
    public DispatchAcceptResult accept(DispatchAcceptCommand command) {

        dispatchDriverPermissionValidator.validate(command.getRole());

        log.info("[Accept] 콜 수락 요청 수신 - driverId={}, dispatchId={}",
                command.getDriverId(), command.getDispatchId());

        Dispatch dispatch = dispatchQueryPort.findById(command.getDispatchId());
        log.debug("[Accept] 조회된 Dispatch 엔티티 - id={}, status={}",
                dispatch.getDispatchId(), dispatch.getDispatchStatus());

        // === from 상태 저장 ===
        DispatchStatus from = dispatch.getDispatchStatus();

        // === 상태 전이 ===
        dispatch.accept();
        DispatchStatus to = dispatch.getDispatchStatus();

        log.info("[Accept] 상태 전이 완료 - id={}, newStatus={}", dispatch.getDispatchId(), to);

        // === 히스토리 기록 ===
        dispatchHistoryCommandPort.save(
                DispatchHistory.recordStatusChange(
                        dispatch.getDispatchId(),
                        String.valueOf(DispatchDomainEventType.ACCEPTED),
                        from,
                        to,
                        ChangedBy.DRIVER,
                        null
                )
        );

        // === 배차 시도 로그 업데이트 ===
        dispatchAssignmentCommandPort.save(
                DispatchAssignmentLog.create(dispatch.getDispatchId(),
                        command.getDriverId())
        );

        // === 저장 ===
        dispatchCommandPort.save(dispatch);

        // === 이벤트 발행 (Outbox) ===
        // Support 이벤트
        dispatchAcceptedCommandPort.publishAccepted(
                DispatchAcceptedPayload.from(dispatch, command.getDriverId())
        );

        // Trip 생성 요청 이벤트 추가
        tripCreateRequestCommandPort.publishTripCreateRequest(
                TripCreateRequestPayload.from(dispatch)
        );


        // === 응답 DTO 생성 ===
        return DispatchAcceptResult.builder()
                .dispatchId(command.getDispatchId())
                .driverId(command.getDriverId())
                .dispatchStatus(to)
                .acceptedAt(LocalDateTime.now())
                .build();
    }


    /**
     * 콜 거절
     *
     * @param command
     * @return
     */
    public DispatchRejectResult reject(DispatchRejectCommand command) {

        log.info("[Reject] 콜 거절 요청 수신 - driverId={}, dispatchId={}",
                command.getDriverId(), command.getDispatchId());

        Dispatch dispatch = dispatchQueryPort.findById(command.getDispatchId());
        log.debug("[Reject] 조회된 Dispatch 엔티티 - id={}, status={}",
                dispatch.getDispatchId(), dispatch.getDispatchStatus());

        // 상태 전이
        dispatch.cancel();
        log.info("[Reject] Dispatch 상태 전이 완료 - id={}, newStatus={}",
                dispatch.getDispatchId(), dispatch.getDispatchStatus());

        dispatchCommandPort.save(dispatch);

        LocalDateTime rejectedAt = LocalDateTime.now();

        //거절 이벤트 발행
        dispatchRejectedCommandPort.publishRejected(
                DispatchRejectedPayload.from(
                        dispatch.getDispatchId(),
                        command.getDriverId(),
                        rejectedAt
                )
        );

        DispatchRejectResult result = DispatchRejectResult.builder()
                .dispatchId(command.getDispatchId())
                .driverId(command.getDriverId())
                .dispatchStatus(DispatchStatus.CANCELLED)
                .rejectedAt(rejectedAt)
                .build();

        log.info("[Reject] 콜 거절 처리 완료 - dispatchId={}, rejectedAt={}",
                result.getDispatchId(), result.getRejectedAt());

        return result;
    }

}
