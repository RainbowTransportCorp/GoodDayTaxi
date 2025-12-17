package com.gooddaytaxi.dispatch.infrastructure.adapter;

import com.gooddaytaxi.dispatch.application.port.out.query.DispatchAssignmentLogQueryPort;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.repository.DispatchAssignmentLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DispatchAssignmentLogQueryAdapter implements DispatchAssignmentLogQueryPort {

    private final DispatchAssignmentLogRepository dispatchAssignmentLogRepository;

    /**
     * 특정 기사에게 배차 제안이 이루어진 ASSIGNING 상태의 배차 목록을 조회한다.
     * @param driverId 배차 제안을 받은 기사 ID
     * @return 기사에게 제안된 미확정 배차 목록
     */
    @Override
    public List<Dispatch> findAssigningByCandidateDriver(UUID driverId) {
        return dispatchAssignmentLogRepository.findPendingByCandidateDriver(driverId);
    }

}
