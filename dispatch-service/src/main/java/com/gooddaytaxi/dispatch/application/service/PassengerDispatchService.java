package com.gooddaytaxi.dispatch.application.service;

import com.gooddaytaxi.dispatch.application.port.out.command.*;
import com.gooddaytaxi.dispatch.application.usecase.cancel.DispatchCancelCommand;
import com.gooddaytaxi.dispatch.application.usecase.create.DispatchCreateCommand;
import com.gooddaytaxi.dispatch.application.event.payload.DispatchCanceledPayload;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.application.usecase.cancel.DispatchCancelResult;
import com.gooddaytaxi.dispatch.application.usecase.create.DispatchCreateResult;
import com.gooddaytaxi.dispatch.application.query.DispatchDetailResult;
import com.gooddaytaxi.dispatch.application.query.DispatchSummaryResult;
import com.gooddaytaxi.dispatch.application.usecase.create.DispatchCreatePermissionValidator;
import com.gooddaytaxi.dispatch.application.exception.auth.DispatchPassengerPermissionValidator;
import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchHistory;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchDomainEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PassengerDispatchService {
    private final DispatchCommandPort dispatchCommandPort;
    private final DispatchHistoryCommandPort dispatchHistoryCommandPort;
    private final DispatchCanceledCommandPort dispatchCanceledCommandPort;

    private final DispatchQueryPort dispatchQueryPort;

    private final DispatchCreatePermissionValidator dispatchCreatePermissionValidator;
    private final DispatchPassengerPermissionValidator dispatchPassengerPermissionValidator;
    // :경광등: 새로 추가: 배차 전담 서비스
    private final DriverAssignmentService driverAssignmentService;
    /**
     * 콜 생성 (승객) —> 상태: REQUESTED
     * 여기서는 절대 기사 조회/배정하지 않는다.
     */
    public DispatchCreateResult create(DispatchCreateCommand command) {
        log.info("[DispatchCreate] 요청 수신 - passengerId={}, pickup={}, destination={}",
                command.getPassengerId(), command.getPickupAddress(), command.getDestinationAddress());

        dispatchCreatePermissionValidator.validate(command.getRole());

        log.debug("[DispatchCreate] 권한 검증 완료 - role={}", command.getRole());

        // 1. Dispatch 생성 (REQUESTED)
        Dispatch dispatch = Dispatch.create(
                command.getPassengerId(),
                command.getPickupAddress(),
                command.getDestinationAddress()
        );
        log.debug("[DispatchCreate] Dispatch 엔티티 생성 완료 - status={}", dispatch.getDispatchStatus());

        Dispatch saved = dispatchCommandPort.save(dispatch);

        log.info("[DispatchCreate] Dispatch 저장 완료 - dispatchId={} status={}",
                saved.getDispatchId(), saved.getDispatchStatus());

        // 2. 히스토리 저장 (CREATED / REQUESTED)
        dispatchHistoryCommandPort.save(
                DispatchHistory.recordStatusChange(
                        saved.getDispatchId(),
                        DispatchDomainEventType.CREATED.name(),
                        null,
                        saved.getDispatchStatus(),   // REQUESTED
                        ChangedBy.PASSENGER,
                        null
                )
        );
        log.debug("[DispatchCreate] 히스토리 기록 완료 - dispatchId={}", saved.getDispatchId());

        // 3. 배차 시도 트리거 (동기 호출 / 나중에 비동기로 바꿀 수 있음)
        driverAssignmentService.assignDriverFor(saved.getDispatchId());

        log.info("[DispatchCreate] 배차 시도 서비스 호출 완료 - dispatchId={}", saved.getDispatchId());

        // 4. 승객에게는 "현재 시점" 상태만 반환 (REQUESTED)
        return DispatchCreateResult.builder()
                .dispatchId(saved.getDispatchId())
                .passengerId(saved.getPassengerId())
                .pickupAddress(saved.getPickupAddress())
                .destinationAddress(saved.getDestinationAddress())
                .dispatchStatus(saved.getDispatchStatus())   // REQUESTED
                .requestCreatedAt(saved.getRequestCreatedAt())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }
    /**
     * 콜 리스트 조회 (승객)
     */
    @Transactional(readOnly = true)
    public List<DispatchSummaryResult> getDispatchList(UUID passengerId, UserRole role) {

        log.info("[DispatchList] 조회 요청 - passengerId={}", passengerId);

        dispatchPassengerPermissionValidator.validate(role);

        log.debug("[DispatchList] 권한 검증 완료 - role={}", role);

        List<Dispatch> dispatches = dispatchQueryPort.findAllByPassengerId(passengerId);

        log.info("[DispatchList] 조회 완료 - count={}", dispatches.size());

        return dispatches.stream()
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
     * 콜 상세조회 (승객)
     */
    @Transactional(readOnly = true)
    public DispatchDetailResult getDispatchDetail(UserRole role, UUID dispatchId) {

        log.info("[DispatchDetail] 조회 요청 - dispatchId={}", dispatchId);

        dispatchPassengerPermissionValidator.validate(role);

        log.debug("[DispatchDetail] 권한 검증 완료 - role={}", role);

        Dispatch dispatch = dispatchQueryPort.findById(dispatchId);

        log.info("[DispatchDetail] 조회 성공 - dispatchId={} status={}",
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
                .cancelledAt(dispatch.getCancelledAt())
                .timeoutAt(dispatch.getTimeoutAt())
                .createdAt(dispatch.getCreatedAt())
                .updatedAt(dispatch.getUpdatedAt())
                .build();
    }
    /**
     * 콜 취소 (승객)
     */
    public DispatchCancelResult cancel(DispatchCancelCommand command) {

        log.info("[DispatchCancel] 요청 수신 - passengerId={}, dispatchId={}",
                command.getPassengerId(), command.getDispatchId());

        dispatchPassengerPermissionValidator.validate(command.getRole());

        log.debug("[DispatchCancel] 권한 검증 완료 - role={}", command.getRole());

        Dispatch dispatch = dispatchQueryPort.findById(command.getDispatchId());

        log.debug("[DispatchCancel] 조회 완료 - currentStatus={}", dispatch.getDispatchStatus());

        dispatch.cancel();

        dispatchCommandPort.save(dispatch);

        log.info("[DispatchCancel] 상태 전이 완료 - dispatchId={} newStatus={}",
                dispatch.getDispatchId(), dispatch.getDispatchStatus());

        dispatchCanceledCommandPort.publishCanceled(
                DispatchCanceledPayload.fromPassenger(dispatch)
        );

        log.info("[DispatchCancel] DISPATCH_CANCELLED 이벤트 발행 - dispatchId={}",
                dispatch.getDispatchId());

        return DispatchCancelResult.builder()
                .dispatchId(dispatch.getDispatchId())
                .dispatchStatus(dispatch.getDispatchStatus())
                .cancelledAt(dispatch.getCancelledAt())
                .build();
    }
}

