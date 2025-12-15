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

    @Override
    public List<Dispatch> findAssigningByCandidateDriver(UUID driverId) {
        return dispatchAssignmentLogRepository.findPendingByCandidateDriver(driverId);
    }

}
