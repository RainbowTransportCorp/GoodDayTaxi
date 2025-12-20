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
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
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
     *
     * @param dispatchId 배차 식별자
     */
    public void assignInitial(UUID dispatchId) {


        Dispatch dispatch = queryPort.findById(dispatchId);
        log.error("[ASSIGN_INITIAL] dispatchId={} status={}",
            dispatch.getDispatchId(),
            dispatch.getDispatchStatus()
        );
        // 최초 상태 전이
        dispatch.startAssigning();
        //배차 횟수 증가
        dispatch.increaseReassignAttempt();
        commandPort.save(dispatch);

        historyService.saveStatusChange(
                dispatchId,
                HistoryEventType.STATUS_CHANGED,
                DispatchStatus.REQUESTED,
                DispatchStatus.ASSIGNING,
                ChangedBy.SYSTEM,
                "최초 배차시도"
        );

        List<UUID> candidates = driverPort.getAvailableDrivers(dispatch.getPickupAddress()).driverIds();
        if (candidates.isEmpty()) throw new DriverUnavailableException();

        sendToDrivers(dispatch, candidates, dispatch.getReassignAttemptCount());
    }

    /**
     * 재배차를 시도
     *
     * @param dispatchId      요청된 배차 식별자
     * @param filteredDrivers Account에서
     */
    public void assignWithFilter(UUID dispatchId, List<UUID> filteredDrivers) {

        Dispatch dispatch = queryPort.findById(dispatchId);

        // 2. 재배차 횟수 증가
        dispatch.increaseReassignAttempt();

        commandPort.save(dispatch);

        // 3. 재배차 히스토리
        historyService.saveStatusChange(
                dispatchId,
                HistoryEventType.REASSIGN_REQUESTED,
                DispatchStatus.ASSIGNING,
                DispatchStatus.ASSIGNING,
                ChangedBy.SYSTEM,
                "재배차 요청 전송 (attempt=" + dispatch.getReassignAttemptCount() + ")"
        );

        // 4. 기사 재요청
        sendToDrivers(
                dispatch,
                filteredDrivers,
                dispatch.getReassignAttemptCount()
        );
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
