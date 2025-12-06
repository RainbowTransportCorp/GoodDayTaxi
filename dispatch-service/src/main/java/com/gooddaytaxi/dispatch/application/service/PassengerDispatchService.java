package com.gooddaytaxi.dispatch.application.service;

import com.gooddaytaxi.dispatch.application.commend.DispatchCancelCommand;
import com.gooddaytaxi.dispatch.application.commend.DispatchCreateCommand;
import com.gooddaytaxi.dispatch.infrastructure.outbox.publisher.DispatchCreatedEventPublisher;
import com.gooddaytaxi.dispatch.application.event.payload.DispatchCreatedPayload;
import com.gooddaytaxi.dispatch.application.port.out.commend.DispatchAssignmentLogCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.commend.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.commend.DispatchHistoryCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.application.result.DispatchCancelResult;
import com.gooddaytaxi.dispatch.application.result.DispatchCreateResult;
import com.gooddaytaxi.dispatch.application.result.DispatchDetailResult;
import com.gooddaytaxi.dispatch.application.result.DispatchListResult;
import com.gooddaytaxi.dispatch.application.validator.RoleValidator;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
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
    private final DispatchAssignmentLogCommandPort dispatchAssignmentCommandPort;

    private final DispatchQueryPort dispatchQueryPort;

    private final DispatchCreatedEventPublisher dispatchCreatedEventPublisher;
    private final RoleValidator roleValidator;
    /**
     * 콜 생성 (승객)
     * @param command
     * @return
     */
    public DispatchCreateResult create(DispatchCreateCommand command) {

        roleValidator.validate(command.getRole());

        log.info("DispatchService.create() 호출됨 - passengerId={}, pickup={}, destination={}",
               command.getPassengerId(), command.getPickupAddress(), command.getDestinationAddress());

        Dispatch entity = Dispatch.create
                (command.getPassengerId(), command.getPickupAddress(), command.getDestinationAddress());

        log.info("생성된 엔티티: {}", entity);
        Dispatch saved = dispatchCommandPort.save(entity);

        // 4. 히스토리 기록 (REQUESTED)
        dispatchHistoryCommandPort.recordStatusChange(saved);

        // 5. 시도 로그 저장
        dispatchAssignmentCommandPort.createAssignmentLog(saved);

        log.info("저장 완료: dispatchId={} / status={}",
                saved.getDispatchId(), saved.getDispatchStatus());

        // 6. 아웃박스 이벤트 저장
        dispatchCreatedEventPublisher.save(
                DispatchCreatedPayload.from(saved)
        );


        // 7. 응답 DTO 생성
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
     * @param
     * @return
     */
    @Transactional(readOnly = true)
    public DispatchListResult getDispatchList (UUID userId) {
        List<Dispatch> dispatches = dispatchQueryPort.findAllByFilter();
        return DispatchListResult.builder().build();
    }

    /**
     * 콜 상세조회 (승객)
     * @param dispatchId
     * @return
     */
    @Transactional(readOnly = true)
    public DispatchDetailResult getDispatchDetail(UUID dispatchId) {

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
