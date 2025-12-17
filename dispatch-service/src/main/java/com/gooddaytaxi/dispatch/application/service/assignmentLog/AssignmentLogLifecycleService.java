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

/**
 * 배차 후보 기사에 대한 DispatchAssignmentLog의
 * 조회 및 상태 관리를 담당하는 라이프사이클 서비스.
 *
 * 배차 수락/거절 서비스에서 후보 기사 로그를 일관된 방식으로 조회하고 저장하기 위해 사용된다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentLogLifecycleService {

    private final DispatchAssignmentLogRepository repository;
    private final DispatchAssignmentCommandPort commandPort;

    /**
     * 특정 배차(dispatchId)에 대해
     * 해당 기사(driverId)의 가장 최근 배차 시도 로그를 조회한다.
     *
     * 배차 시도 로그가 없을 시 AssignmentLogNotFoundException 예외 발생
     * @param dispatchId 특정 배차 식별자
     * @param driverId 특정 기사 식별자
     * @return 조회된 DispatchAssignmentLog 정보
     */
    public DispatchAssignmentLog findLatest(UUID dispatchId, UUID driverId) {

        return repository.findLatest(dispatchId, driverId)
                .orElseThrow(() ->
                        new AssignmentLogNotFoundException(dispatchId, driverId)
                );

    }

    /**
     *
     * @param logEntry
     */
    public void save(DispatchAssignmentLog logEntry) {

        commandPort.save(logEntry);

        log.debug("[assignmentLog] 저장 - dispatchId={} driverId={} status={}",
                logEntry.getDispatchId(),
                logEntry.getCandidateDriverId(),
                logEntry.getAssignmentStatus()
        );
    }
}
