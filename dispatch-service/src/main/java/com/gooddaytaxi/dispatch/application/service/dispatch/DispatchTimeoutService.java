package com.gooddaytaxi.dispatch.application.service.dispatch;

import com.gooddaytaxi.dispatch.application.event.payload.DispatchTimeoutPayload;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.application.service.assignmentLog.AssignmentLogQueryService;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchDomainEventType;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import com.gooddaytaxi.dispatch.domain.model.enums.HistoryEventType;
import com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.publisher.DispatchTimeoutOutboxPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchTimeoutService {

    private final DispatchQueryPort queryPort;
    private final DispatchCommandPort commandPort;

    private final DispatchDriverAssignmentService reassignService;
    private final DispatchHistoryService historyService;
    private final RetryPolicyService retryPolicyService;
    private final AssignmentLogQueryService assignmentLogQueryService;

    private final DispatchTimeoutOutboxPublisher eventPort;

    private static final int REASSIGN_TIMEOUT_SECONDS = 60;
    private static final int FINAL_TIMEOUT_SECONDS = 600;

    public void runTimeoutCheck() {

        log.debug("[TIMEOUT] 체크 시작");

        List<Dispatch> targets = queryPort.findTimeoutCandidates();

        for (Dispatch dispatch : targets) {
            try {
                handleTimeout(dispatch);

            } catch (Exception e) {
                log.error("[TIMEOUT] 처리 중 오류 - dispatchId={} error={}",
                        dispatch.getDispatchId(), e.getMessage(), e);
            }
        }
    }

    private void handleTimeout(Dispatch dispatch) {

        if (dispatch.getDispatchStatus().isTerminal()) {
            log.debug(
                    "[TIMEOUT-SKIP] 이미 종료된 dispatch - dispatchId={} status={}",
                    dispatch.getDispatchId(),
                    dispatch.getDispatchStatus()
            );
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        long elapsedTotal = Duration.between(dispatch.getRequestCreatedAt(), now).getSeconds();
        long elapsedSinceAssigning = Duration.between(dispatch.getUpdatedAt(), now).getSeconds();

        // ============================================================
        // 1) 최종 타임아웃
        // ============================================================
        if (elapsedTotal >= FINAL_TIMEOUT_SECONDS) {
            forceTimeoutByRetryLimit(dispatch);
            return;
        }

        // ============================================================
        // 2) ASSIGNING / ASSIGNED → 재배차 판단
        // ============================================================
        //이쪽 예외처리...상태 판단에 대한 로직을 엔티티로 뺄 예정
        if ((dispatch.getDispatchStatus() == DispatchStatus.ASSIGNING
                || dispatch.getDispatchStatus() == DispatchStatus.ASSIGNED)
                && elapsedSinceAssigning >= REASSIGN_TIMEOUT_SECONDS) {

            log.info(
                    "[TIMEOUT-REASSIGN] 재배차 판단 - dispatchId={} status={} elapsed={}s",
                    dispatch.getDispatchId(),
                    dispatch.getDispatchStatus(),
                    elapsedSinceAssigning
            );

            // --- 재배차 한계 초과 → 종료 ---
            if (retryPolicyService.isRetryLimitExceeded(dispatch)) {
                forceTimeoutByRetryLimit(dispatch);
                return;
            }

            // --- 재배차 진행 ---
            DispatchStatus before = dispatch.getDispatchStatus();

            dispatch.increaseReassignAttempt();
            commandPort.save(dispatch);

            historyService.saveStatusChange(
                    dispatch.getDispatchId(),
                    HistoryEventType.STATUS_CHANGED,
                    before,
                    DispatchStatus.ASSIGNING,
                    ChangedBy.SYSTEM
            );

            List<UUID> excludeDrivers =
                    assignmentLogQueryService.findPreviouslyTriedDrivers(dispatch.getDispatchId());

            reassignService.assignWithFilter(
                    dispatch.getDispatchId(),
                    dispatch.getReassignAttemptCount(),
                    excludeDrivers
            );
        }
    }


    private void forceTimeoutByRetryLimit(Dispatch dispatch) {

        DispatchStatus before = dispatch.getDispatchStatus();

        log.warn(
                "[TIMEOUT-RETRY-LIMIT] 재배차 한계 초과로 종료 처리 - dispatchId={} before={}",
                dispatch.getDispatchId(),
                before
        );

        dispatch.terminateByRetryLimit();
        commandPort.save(dispatch);

        historyService.saveStatusChange(
                dispatch.getDispatchId(),
                HistoryEventType.TIMEOUT,
                before,
                DispatchStatus.TIMEOUT,
                ChangedBy.SYSTEM
        );

        eventPort.publishTimeout(
                new DispatchTimeoutPayload(
                        dispatch.getDispatchId(),
                        dispatch.getTimeoutAt()
                )
        );
    }
}
