package com.gooddaytaxi.dispatch.application.service.dispatch;

import com.gooddaytaxi.dispatch.application.event.payload.DispatchRequestedPayload;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchAssignmentCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchRequestedCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.query.AccountDriverSelectionQueryPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.domain.exception.DriverUnavailableException;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchAssignmentLog;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchDomainEventType;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import com.gooddaytaxi.dispatch.infrastructure.client.account.dto.DriverInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchDriverAssignmentService {

    private final DispatchQueryPort queryPort;
    private final DispatchCommandPort commandPort;
    private final AccountDriverSelectionQueryPort driverPort;

    private final DispatchAssignmentCommandPort assignmentLogPort;
    private final DispatchHistoryService historyService;

    private final DispatchRequestedCommandPort eventPort;

    public void assign(UUID dispatchId) {

        log.info("[Assign] 배차 시도 시작 - dispatchId={}", dispatchId);

        // 1. 조회
        Dispatch dispatch = queryPort.findById(dispatchId);

        // 2. 도메인 규칙에 의해 상태 전이 검증 (서비스는 if 필요 없음)
        DispatchStatus before = dispatch.getDispatchStatus();
        dispatch.startAssigning();     // ← Invalid 상태면 도메인이 예외 던짐

        // 저장
        commandPort.save(dispatch);

        // 3. 히스토리
        historyService.saveStatusChange(
                dispatchId,
                DispatchDomainEventType.ASSIGNING,
                before,
                dispatch.getDispatchStatus(),
                ChangedBy.SYSTEM
        );

        log.info("[Assign] 상태전이 REQUESTED → ASSIGNING");

        // 4. 기사 후보 조회
        DriverInfo candidates =
                driverPort.getAvailableDrivers(dispatch.getPickupAddress());

        if (candidates.driverIds().isEmpty()) {
            log.warn("[Assign] 배차 가능한 기사 없음 - dispatchId={}", dispatchId);
            throw new DriverUnavailableException();
        }

        log.info("[Assign] 후보 기사 수={}, region={}",
                candidates.driverIds().size(),
                candidates.region()
        );

        // 5. 기사별 배차 요청
        for (UUID driverId : candidates.driverIds()) {
            log.info("[Assign] 기사에게 배차 요청 전송 - dispatchId={} driverId={}",
                    dispatchId, driverId);

            // (1) 이벤트 발행 - 핵심
            eventPort.publishRequested(
                    new DispatchRequestedPayload(
                            dispatch.getDispatchId(),
                            dispatch.getPassengerId(),
                            driverId,
                            dispatch.getPassengerId(),
                            dispatch.getPickupAddress(),
                            dispatch.getDestinationAddress(),
                            "새로운 콜 요청이 도착했습니다."
                    )
            );

            // (2) assignment log 기록 - 부가적
            try {
                DispatchAssignmentLog logEntity =
                        DispatchAssignmentLog.create(dispatchId, driverId);

                assignmentLogPort.save(logEntity);

            } catch (Exception e) {
                log.error("[Assign] AssignmentLog 저장 실패 - dispatchId={} driverId={} error={}",
                        dispatchId, driverId, e.getMessage());
                // 흐름은 멈추지 않음
            }
        }

        log.info("[Assign] 배차 요청 이벤트 발행 완료 - dispatchId={} targetDrivers={}",
                dispatchId, candidates.driverIds().size());
    }
}
