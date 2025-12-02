package com.gooddaytaxi.dispatch.application.service;

import com.gooddaytaxi.dispatch.application.commend.DispatchCreateCommand;
import com.gooddaytaxi.dispatch.application.port.out.commend.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.application.result.DispatchCreateResult;
import com.gooddaytaxi.dispatch.application.result.DispatchDetailResult;
import com.gooddaytaxi.dispatch.application.result.DispatchListResult;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import jakarta.ws.rs.NotFoundException;
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
public class DispatchService {

    private final DispatchCommandPort dispatchcommandPort;
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
                .dispatchStatus(DispatchStatus.REQUESTED)
                .build();

        log.info("생성된 엔티티: {}", entity);

        Dispatch saved = dispatchcommandPort.save(entity);

        log.info("저장 완료: dispatchId={} / status={}",
                saved.getDispatch_id(), saved.getDispatchStatus());

        return DispatchCreateResult.builder()
                .dispatchId(saved.getDispatch_id())
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
    public DispatchListResult getDispatchList (UUID userId) {
        List<Dispatch> dispatches = dispatchQueryPort.findAllByFilter();
        return DispatchListResult.builder().build();
    }

    /**
     * 콜 상세조회 (승객)
     * @param dispatchId
     * @return
     */
    public DispatchDetailResult getDispatchDetail(UUID dispatchId) {

        Dispatch dispatch = dispatchQueryPort.findById(dispatchId)
                .orElseThrow(NotFoundException::new);

        return DispatchDetailResult.builder()
                .dispatchId(dispatch.getDispatch_id())
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
}
