package com.gooddaytaxi.dispatch.application.service;

import com.gooddaytaxi.dispatch.application.event.payload.DispatchRequestedPayload;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchAssignmentCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchHistoryCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchRequestedCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.query.AccountDriverSelectionQueryPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.domain.exception.DriverUnavailableException;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchAssignmentLog;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchHistory;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchDomainEventType;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import com.gooddaytaxi.dispatch.domain.repository.DispatchAssignmentLogRepository;
import com.gooddaytaxi.dispatch.infrastructure.client.account.dto.DriverInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DriverAssignmentService {

    private final DispatchQueryPort dispatchQueryPort;
    private final DispatchCommandPort dispatchCommandPort;

    private final AccountDriverSelectionQueryPort driverSelectionPort;
    private final DispatchAssignmentCommandPort assignmentLogPort;
    private final DispatchHistoryCommandPort historyPort;

    private final DispatchAssignmentLogRepository dispatchAssignmentLogRepository;

    private final DispatchRequestedCommandPort requestedEventPort;

    /**
     * 단일 배차에 대해 기사 배차 시도 시작
     * - 상태: REQUESTED → ASSIGNING
     * - 기사 후보 조회
     * - 각 기사에게 배차요청 이벤트 발행
     * - 배차 시도 로그 기록
     */
    public void assignDriverFor(UUID dispatchId) {

        log.info("[Assign] 배차 시도 시작 - dispatchId={}", dispatchId);

        // 1. Dispatch 조회
        Dispatch dispatch = dispatchQueryPort.findById(dispatchId);

        if (dispatch.getDispatchStatus() != DispatchStatus.REQUESTED) {
            log.warn("[Assign] 배차 시도 불가 상태 - dispatchId={} status={}",
                    dispatchId, dispatch.getDispatchStatus());
            return;
        }

        // 2. 기사 후보 조회
        DriverInfo candidates =
                driverSelectionPort.getAvailableDrivers(dispatch.getPickupAddress());

        if (candidates.driverIds().isEmpty()) {
            log.warn("[Assign] 배차 가능한 기사 없음 - dispatchId={}", dispatchId);
            // 정책상 여기서 timeout/cancel 등으로 바꿀 수도 있음
            throw new DriverUnavailableException();
        }

        log.info("[Assign] 후보 기사 수={} region={}",
                candidates.totalCount(), candidates.region());

        // 3. 상태 전이: REQUESTED -> ASSIGNING
        DispatchStatus from = dispatch.getDispatchStatus(); // REQUESTED


        dispatch.startAssigning();

        DispatchStatus to = dispatch.getDispatchStatus();   // ASSIGNING

        dispatchCommandPort.save(dispatch);
        log.info("[Assign] 상태 전이 완료 - dispatchId={} {} -> {}",
                dispatchId, from, to);

        // 4. 히스토리 기록
        historyPort.save(
                DispatchHistory.recordStatusChange(
                        dispatchId,
                        DispatchDomainEventType.ASSIGNING.name(),
                        from,
                        to,
                        ChangedBy.SYSTEM,
                        null
                )
        );
        log.debug("[Assign] 히스토리 기록 완료 - dispatchId={}", dispatchId);

        // 5. 각 기사에게 배차 시도 + 로그 + 이벤트 발행
        for (UUID driverId : candidates.driverIds()) {

            log.info("[Assign] 기사에게 배차 요청 전송 - dispatchId={} driverId={}",
                    dispatchId, driverId);

            // === AssignmentLog 상태 전이 ===
            DispatchAssignmentLog assignmentLog =
                    dispatchAssignmentLogRepository.findLatest(dispatchId, driverId)
                            .orElseThrow(() -> new IllegalStateException("Assignment log missing"));

            assignmentLog.timeout(); // SENT → TIMEOUT
            assignmentLogPort.save(assignmentLog);

            // === Dispatch 상태 전이 ===
            dispatch.timeout();
            dispatchCommandPort.save(dispatch);

            // === 히스토리 기록 ===
            historyPort.save(
                    DispatchHistory.recordStatusChange(
                            dispatchId,
                            DispatchDomainEventType.TIMEOUT.name(),
                            from,
                            DispatchStatus.TIMEOUT,
                            ChangedBy.SYSTEM,
                            "driver did not respond"
                    )
            );

            // Support-service로 알림 이벤트 발행
            requestedEventPort.publishRequested(
                    new DispatchRequestedPayload(
                            dispatch.getDispatchId(),
                            dispatch.getPassengerId(), // notifierId
                            driverId,
                            dispatch.getPassengerId(),  // 알림 메시지용 정보
                            dispatch.getPickupAddress(),
                            dispatch.getDestinationAddress(),
                            "새로운 콜 요청이 도착했습니다."
                    )
            );
        }

        log.info("[Assign] 배차 요청 이벤트 발행 완료 - dispatchId={} targetDrivers={}",
                dispatchId, candidates.driverIds().size());
    }
}
