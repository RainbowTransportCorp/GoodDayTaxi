package com.gooddaytaxi.dispatch.application.service.dispatch;

import com.gooddaytaxi.dispatch.application.event.payload.DispatchRequestedPayload;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchAssignmentCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchRequestedCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.query.AccountDriverSelectionQueryPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.application.exception.DriverUnavailableException;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchAssignmentLog;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import com.gooddaytaxi.dispatch.domain.model.enums.HistoryEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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

    /**
     * 최초의 배차 시도
     * @param dispatchId 배차 식별자
     */
    public void assignInitial(UUID dispatchId) {

        Dispatch dispatch = queryPort.findById(dispatchId);

        dispatch.startAssigning(); // 최초 상태 전이
        commandPort.save(dispatch);

        historyService.saveStatusChange(
                dispatchId,
                HistoryEventType.STATUS_CHANGED,
                DispatchStatus.REQUESTED,
                DispatchStatus.ASSIGNING,
                ChangedBy.SYSTEM,
                "최초 배차시도"
        );

        int attemptNo = 1; // 최초는 무조건 1

        List<UUID> candidates = driverPort.getAvailableDrivers(dispatch.getPickupAddress()).driverIds();
        if (candidates.isEmpty()) throw new DriverUnavailableException();

        sendToDrivers(dispatch, candidates, attemptNo);
    }

    /**
     * 재배차 시도
     * @param dispatchId
     * @param attemptNo
     * @param filteredDrivers
     */
    public void assignWithFilter(UUID dispatchId, int attemptNo, List<UUID> filteredDrivers) {

        Dispatch dispatch = queryPort.findById(dispatchId);
        DispatchStatus before = dispatch.getDispatchStatus();

        if (before == DispatchStatus.ASSIGNED) {
            // ACCEPT 없이 시간이 지나서 재배차하는 경우
            dispatch.resetToAssigning();
            commandPort.save(dispatch);

            historyService.saveStatusChange(
                    dispatchId,
                    HistoryEventType.STATUS_CHANGED,
                    DispatchStatus.ASSIGNED,
                    DispatchStatus.ASSIGNING,
                    ChangedBy.SYSTEM,
                    "시간초과로 인한 재배차"
            );

        } else if (before == DispatchStatus.ASSIGNING) {
            // 이미 ASSIGNING이면 state transition 불필요
            log.debug("[Assign] ASSIGNING 상태 유지 - resetToAssigning() 생략");
        } else {
            // 그 외 상태(REQUESTED, TIMEOUT 등)는 재배차 불가
            log.warn("[Assign] 재배차 불가 상태={} - dispatchId={}", before, dispatchId);
            return;
        }

        // ====================================================
        // 필터링된 기사 대상 배차 요청
        // ====================================================
        if (filteredDrivers.isEmpty()) {
            log.warn("[Assign] 재배차 대상 기사 없음 - dispatchId={}", dispatchId);
            return;
        }

        sendToDrivers(dispatch, filteredDrivers, attemptNo);
    }


    private void sendToDrivers(Dispatch dispatch, List<UUID> drivers, int attemptNo) {

        for (UUID driverId : drivers) {

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

            try {
                DispatchAssignmentLog logEntity =
                        DispatchAssignmentLog.create(dispatch.getDispatchId(), driverId, attemptNo);

                assignmentLogPort.save(logEntity);

            } catch (Exception e) {
                log.error("[Assign] assignmentLog 저장 실패 - dispatchId={} driverId={} err={}",
                        dispatch.getDispatchId(), driverId, e.getMessage());
            }
        }
    }
}
