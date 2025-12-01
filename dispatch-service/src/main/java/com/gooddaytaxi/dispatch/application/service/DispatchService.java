package com.gooddaytaxi.dispatch.application.service;

import com.gooddaytaxi.dispatch.application.commend.DispatchCreateCommand;
import com.gooddaytaxi.dispatch.application.port.out.commend.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.result.DispatchCreateResult;
import com.gooddaytaxi.dispatch.application.result.DispatchListResult;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DispatchService {

    private final DispatchCommandPort commandPort;

    public DispatchCreateResult create(Long passengerId, DispatchCreateCommand command) {

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

        Dispatch saved = commandPort.save(entity);

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

}
