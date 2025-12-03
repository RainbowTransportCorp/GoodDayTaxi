package com.gooddaytaxi.dispatch.application.service;

import com.gooddaytaxi.dispatch.application.commend.DispatchCancelCommand;
import com.gooddaytaxi.dispatch.application.commend.DispatchCreateCommand;
import com.gooddaytaxi.dispatch.application.port.out.commend.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.application.result.*;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus.REQUESTED;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DispatchService {

    private final DispatchCommandPort dispatchCommandPort;

    private final DispatchQueryPort dispatchQueryPort;

    /**
     * 콜 생성 (승객)
     * @param passengerId
     * @param command
     * @return
     */
    public DispatchCreateResult create(UUID passengerId, DispatchCreateCommand command) {

        log.info("DispatchService.create() 호출됨 - passengerId={}, pickup={}, destination={}",
                passengerId, command.getPickupAddress(), command.getDestinationAddress());

        Dispatch entity = Dispatch.builder()
                .passengerId(passengerId)
                .pickupAddress(command.getPickupAddress())
                .destinationAddress(command.getDestinationAddress())
                .requestCreatedAt(LocalDateTime.now())
                .dispatchStatus(REQUESTED)
                .build();

        log.info("생성된 엔티티: {}", entity);
        Dispatch saved = dispatchCommandPort.save(entity);

//        // 승객용 이벤트 기록 (“배차 요청이 접수되었습니다”)


        log.info("저장 완료: dispatchId={} / status={}",
                saved.getDispatchId(), saved.getDispatchStatus());

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

    /**
     * 배차 대기 (ASSIGNING) 상태 콜 조회 (기사)
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

}
