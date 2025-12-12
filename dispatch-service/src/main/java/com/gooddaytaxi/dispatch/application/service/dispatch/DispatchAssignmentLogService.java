package com.gooddaytaxi.dispatch.application.service.dispatch;

import com.gooddaytaxi.dispatch.application.port.out.command.DispatchAssignmentCommandPort;
import com.gooddaytaxi.dispatch.domain.exception.InvalidAssignmentStatusException;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchAssignmentLog;
import com.gooddaytaxi.dispatch.domain.model.enums.AssignmentStatus;
import com.gooddaytaxi.dispatch.domain.repository.DispatchAssignmentLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchAssignmentLogService {

    private final DispatchAssignmentLogRepository repository;
    private final DispatchAssignmentCommandPort commandPort;

    /**
     * 배차 시도 로그 생성 (SENT)
     */
    public DispatchAssignmentLog create(UUID dispatchId, UUID driverId) {

        DispatchAssignmentLog logEntity = DispatchAssignmentLog.create(dispatchId, driverId);

        commandPort.save(logEntity);

        log.debug("[AssignmentLog] 생성 - dispatchId={} driverId={} status={}",
                dispatchId, driverId, logEntity.getAssignmentStatus());

        return logEntity;
    }

    /**
     * 특정 dispatchId + driverId 기준 최신 AssignmentLog 조회
     * - Accept/Reject 시 필수
     */
    public DispatchAssignmentLog findLatest(UUID dispatchId, UUID driverId) {

        return repository.findLatest(dispatchId, driverId)
                .orElseThrow(
                        () -> new InvalidAssignmentStatusException(
                                "해당 기사에게 발송된 AssignmentLog가 존재하지 않습니다. " +
                                        "dispatchId=" + dispatchId + ", driverId=" + driverId
                        )
                );
    }

    /**
     * 로그 저장 (Accept, Reject, Timeout 등)
     */
    public void save(DispatchAssignmentLog logEntry) {

        commandPort.save(logEntry);

        log.debug("[AssignmentLog] 저장 - dispatchId={} driverId={} status={}",
                logEntry.getDispatchId(),
                logEntry.getCandidateDriverId(),
                logEntry.getAssignmentStatus()
        );
    }

    /**
     * SENT 상태인지 여부
     * (유스케이스 단에서 부가 검사하려면 사용)
     */
    public boolean isLatestSent(UUID dispatchId, UUID driverId) {
        return repository.findLatest(dispatchId, driverId)
                .map(log -> log.getAssignmentStatus() == AssignmentStatus.SENT)
                .orElse(false);
    }
}
