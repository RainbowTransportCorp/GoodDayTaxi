package com.gooddaytaxi.dispatch.application.service;

import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchHistoryCommandPort;
import com.gooddaytaxi.dispatch.application.usecase.create.DispatchCreateCommand;
import com.gooddaytaxi.dispatch.application.usecase.create.DispatchCreatePermissionValidator;
import com.gooddaytaxi.dispatch.application.usecase.create.DispatchCreateResult;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchHistory;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchDomainEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchCreateService {

    private final DispatchCommandPort dispatchCommandPort;
    private final DispatchHistoryCommandPort historyPort;
    private final DispatchAssignService dispatchAssignService;

    private final DispatchCreatePermissionValidator dispatchCreatePermissionValidator;

    public DispatchCreateResult create(DispatchCreateCommand command) {

        log.info("[DispatchCreate] 요청 수신 - passengerId={}, pickup={}, destination={}",
                command.getPassengerId(), command.getPickupAddress(), command.getDestinationAddress());

        dispatchCreatePermissionValidator.validate(command.getRole());

        log.debug("[DispatchCreate] 권한 검증 완료 - role={}", command.getRole());


        Dispatch dispatch = Dispatch.create(
                command.getPassengerId(),
                command.getPickupAddress(),
                command.getDestinationAddress()
        );

        log.debug("[DispatchCreate] Dispatch 엔티티 생성 완료 - status={}", dispatch.getDispatchStatus());

        Dispatch saved = dispatchCommandPort.save(dispatch);

        log.info("[DispatchCreate] Dispatch 저장 완료 - dispatchId={} status={}",
                saved.getDispatchId(), saved.getDispatchStatus());


        historyPort.save(
                DispatchHistory.recordStatusChange(
                        saved.getDispatchId(),
                        DispatchDomainEventType.CREATED.name(),
                        null,
                        saved.getDispatchStatus(),
                        ChangedBy.PASSENGER,
                        null
                )
        );

        log.debug("[DispatchCreate] 히스토리 기록 완료 - dispatchId={}", saved.getDispatchId());

        dispatchAssignService.assign(saved.getDispatchId());

        log.info("[DispatchCreate] 배차 시도 서비스 호출 완료 - dispatchId={}", saved.getDispatchId());


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
}
