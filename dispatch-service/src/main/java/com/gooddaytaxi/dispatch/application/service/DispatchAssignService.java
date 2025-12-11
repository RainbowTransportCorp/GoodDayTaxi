package com.gooddaytaxi.dispatch.application.service;

import com.gooddaytaxi.dispatch.application.event.payload.DispatchRequestedPayload;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchAssignmentCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchHistoryCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchRequestedCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.query.AccountDriverSelectionQueryPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchAssignmentLog;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchHistory;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchDomainEventType;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import com.gooddaytaxi.dispatch.infrastructure.client.account.dto.DriverInfo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DispatchAssignService {

    private final DispatchQueryPort queryPort;
    private final DispatchCommandPort commandPort;
    private final AccountDriverSelectionQueryPort driverPort;

    private final DispatchAssignmentLogService assignmentLogService;
    private final DispatchHistoryService historyService;

    private final DispatchRequestedCommandPort eventPort;

    private static final Logger log = LoggerFactory.getLogger(DispatchAssignService.class);

    public void assign(UUID dispatchId) {

        log.info("[Assign] 배차 시도 시작 - dispatchId={}", dispatchId);

        Dispatch dispatch = queryPort.findById(dispatchId);

        if (!dispatch.isRequested()) {
            log.warn("[Assign] 상태 불일치 (배차 시도 불가) - dispatchId={} status={}",
                    dispatchId, dispatch.getDispatchStatus());
            return;
        }

        DispatchStatus before = dispatch.getDispatchStatus();

        dispatch.startAssigning();
        commandPort.save(dispatch);

        //히스토리 기록
        historyService.saveStatusChange(
                dispatchId,
                DispatchDomainEventType.ASSIGNING,
                before,
                dispatch.getDispatchStatus(),
                ChangedBy.SYSTEM
        );

        log.info("[Assign] 상태전이 REQUESTED → ASSIGNING");

        DriverInfo candidates =
                driverPort.getAvailableDrivers(dispatch.getPickupAddress());

        log.info("[Assign] 후보 기사 수={} region={}",
                candidates.driverIds().size(),
                candidates.region()
        );

        for (UUID driverId : candidates.driverIds()) {

            log.info("[Assign] 기사에게 배차 요청 전송 - dispatchId={} driverId={}",
                    dispatchId, driverId);

            assignmentLogService.create(dispatchId, driverId);

            eventPort.publishRequested(
                    new DispatchRequestedPayload(
                            dispatch.getDispatchId(),        // notificationOriginId
                            dispatch.getPassengerId(),       // notifierId
                            driverId,                        // driverId
                            dispatch.getPassengerId(),       // passengerId
                            dispatch.getPickupAddress(),     // pickupAddress
                            dispatch.getDestinationAddress(),// destinationAddress
                            "새로운 콜이 도착했습니다."        // message
                    )
            );
        }

        log.info("[Assign] 배차 요청 이벤트 발행 완료 - dispatchId={} targetDrivers={}",
                dispatchId, candidates.driverIds().size());
    }
}

