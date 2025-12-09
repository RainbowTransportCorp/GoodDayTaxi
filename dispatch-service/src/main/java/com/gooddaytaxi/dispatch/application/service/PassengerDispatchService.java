package com.gooddaytaxi.dispatch.application.service;

import com.gooddaytaxi.dispatch.application.commend.DispatchCancelCommand;
import com.gooddaytaxi.dispatch.application.commend.DispatchCreateCommand;
import com.gooddaytaxi.dispatch.application.event.payload.DispatchRequestedPayload;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchHistoryCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchRequestedCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DriverSelectionQueryPort;
import com.gooddaytaxi.dispatch.application.result.DispatchCancelResult;
import com.gooddaytaxi.dispatch.application.result.DispatchCreateResult;
import com.gooddaytaxi.dispatch.application.result.DispatchDetailResult;
import com.gooddaytaxi.dispatch.application.result.DispatchSummaryResult;
import com.gooddaytaxi.dispatch.application.validator.DispatchCreatePermissionValidator;
import com.gooddaytaxi.dispatch.application.validator.DispatchPassengerPermissionValidator;
import com.gooddaytaxi.dispatch.application.validator.UserRole;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchAssignmentLog;
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
    private final DispatchRequestedCommandPort dispatchRequestedCommandPort;

    private final DispatchQueryPort dispatchQueryPort;
    private final DriverSelectionQueryPort driverSelectionQueryPort;


    private final DispatchCreatePermissionValidator dispatchCreatePermissionValidator;
    private final DispatchPassengerPermissionValidator dispatchPassengerPermissionValidator;

    /**
     * 콜 생성 (승객)
     *
     * @param command
     * @return
     */
    public DispatchCreateResult create(DispatchCreateCommand command) {

        dispatchCreatePermissionValidator.validate(command.getRole());

        log.info("DispatchService.create() 호출됨 - passengerId={}, pickup={}, destination={}",
                command.getPassengerId(), command.getPickupAddress(), command.getDestinationAddress());

        Dispatch entity = Dispatch.create
                (command.getPassengerId(), command.getPickupAddress(), command.getDestinationAddress());

        log.info("생성된 엔티티: {}", entity);
        Dispatch saved = dispatchCommandPort.save(entity);

        // 4. 히스토리 기록 (REQUESTED)
        dispatchHistoryCommandPort.save(
                DispatchHistory.recordStatusChange(
                        saved.getDispatchId(),
                        DispatchDomainEventType.CREATED.name(),
                        null,                      // fromStatus
                        saved.getDispatchStatus(), // toStatus = REQUESTED
                        ChangedBy.PASSENGER,
                        null
                )
        );


        log.info("저장 완료: dispatchId={} / status={}",
                saved.getDispatchId(), saved.getDispatchStatus());


        // 6. 기사 조회 -> 페인 entity 조회로 로직 변경 (이벤트 x)
//        driverSelectionQueryPort.selectCandidateDriver(saved);

        //임시로 기사 id 발생
        UUID randomDriverId = UUID.randomUUID();

        // 시도 로그 저장
        DispatchAssignmentLog.create(saved.getDispatchId(), saved.getDriverId());

        // 7. support 쪽에 '콜 생성했으니 기사에게 알림을 보내세요'용 Requested 이벤트 발행
        dispatchRequestedCommandPort.publishRequested(
                DispatchRequestedPayload.from(
                        saved.getDispatchId(),
                        saved.getPassengerId(),
                        randomDriverId,
                        saved.getPickupAddress(),
                        saved.getDestinationAddress(),
                        "새로운 콜 요청이 도착했습니다."
                )
        );

        //  응답 DTO 생성
        return DispatchCreateResult.builder()
                .dispatchId(saved.getDispatchId())
                .passengerId(saved.getPassengerId())
                .pickupAddress(saved.getPickupAddress())
                .destinationAddress(saved.getDestinationAddress())
                .dispatchStatus(saved.getDispatchStatus())
                .requestCreatedAt(saved.getRequestCreatedAt())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    /**
     * 콜 전체 조회 (승객)
     *
     * @param
     * @return
     */
    @Transactional(readOnly = true)
    public List<DispatchSummaryResult> getDispatchList(UUID userId, UserRole role) {
        dispatchPassengerPermissionValidator.validate(role);

        List<Dispatch> dispatches = dispatchQueryPort.findAllByFilter(userId);
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
     *
     * @param dispatchId
     * @return
     */
    @Transactional(readOnly = true)
    public DispatchDetailResult getDispatchDetail( UserRole role , UUID dispatchId) {

        dispatchPassengerPermissionValidator.validate(role);

        Dispatch dispatch = dispatchQueryPort.findById(dispatchId);

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
     *
     * @param command
     * @return
     */
    public DispatchCancelResult cancel(DispatchCancelCommand command) {

        Dispatch dispatch = dispatchQueryPort.findById(command.getDispatchId());

        dispatch.cancel();

        dispatchCommandPort.save(dispatch);

        return DispatchCancelResult.builder()
                .dispatchId(dispatch.getDispatchId())
                .dispatchStatus(dispatch.getDispatchStatus())
                .cancelledAt(dispatch.getCancelledAt())
                .build();
    }
}
