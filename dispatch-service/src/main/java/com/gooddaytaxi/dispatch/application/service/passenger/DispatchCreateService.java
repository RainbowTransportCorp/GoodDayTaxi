package com.gooddaytaxi.dispatch.application.service.passenger;

import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchHistoryCommandPort;
import com.gooddaytaxi.dispatch.application.service.dispatch.DispatchDriverAssignmentService;
import com.gooddaytaxi.dispatch.application.usecase.create.DispatchCreateCommand;
import com.gooddaytaxi.dispatch.application.usecase.create.DispatchCreatePermissionValidator;
import com.gooddaytaxi.dispatch.application.usecase.create.DispatchCreateResult;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchHistory;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
import com.gooddaytaxi.dispatch.domain.model.enums.HistoryEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchCreateService {

    private final DispatchCommandPort dispatchCommandPort;
    private final DispatchHistoryCommandPort historyPort;
    private final DispatchDriverAssignmentService assignService;

    private final DispatchCreatePermissionValidator dispatchCreatePermissionValidator;

    /**
     * 승객이 배차(콜) 호출
     * @param command 배차를 요청하는 승객과 주소정보
     * @return
     */
    public DispatchCreateResult create(DispatchCreateCommand command) {

        log.info("[DispatchCreate] 요청 수신 - passengerId={}, pickup={}, destination={}",
                command.getPassengerId(), command.getPickupAddress(), command.getDestinationAddress());

        dispatchCreatePermissionValidator.validate(command.getRole());

        Dispatch dispatch = Dispatch.create(
                command.getPassengerId(),
                command.getPickupAddress(),
                command.getDestinationAddress()
        );

        Dispatch saved = dispatchCommandPort.save(dispatch);

        // 히스토리는 실패해도 흐름 유지
        try {
            historyPort.save(
                    DispatchHistory.recordStatusChange(
                            saved.getDispatchId(),
                            HistoryEventType.USER_CREATED.name(),
                            null,
                            saved.getDispatchStatus(),
                            ChangedBy.PASSENGER,
                            "승객으로부터 콜 생성"
                    )
            );
        } catch (Exception e) {
            log.error("[DispatchCreate] 히스토리 기록 실패 - dispatchId={} err={}",
                    saved.getDispatchId(), e.getMessage());
        }

        assignService.assignInitial(saved.getDispatchId());

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
}
