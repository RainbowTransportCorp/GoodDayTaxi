package com.gooddaytaxi.dispatch.application.service.assignmentLog;

import com.gooddaytaxi.dispatch.application.port.out.command.DispatchAssignmentCommandPort;
import com.gooddaytaxi.dispatch.domain.exception.assignment.AssignmentLogNotFoundException;
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
public class AssignmentLogLifecycleService {

    private final DispatchAssignmentLogRepository repository;
    private final DispatchAssignmentCommandPort commandPort;

    /**
     * 특정 dispatchId + driverId 기준 최신 assignmentLog 조회
     * - Accept/Reject 시 필수
     */
    public DispatchAssignmentLog findLatest(UUID dispatchId, UUID driverId) {

        return repository.findLatest(dispatchId, driverId)
                .orElseThrow(() ->
                        new AssignmentLogNotFoundException(dispatchId, driverId)
                );

    }

    /**
     * 로그 저장 (Accept, Reject, Timeout 등)
     */
    public void save(DispatchAssignmentLog logEntry) {

        commandPort.save(logEntry);

        log.debug("[assignmentLog] 저장 - dispatchId={} driverId={} status={}",
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
