package com.gooddaytaxi.dispatch.infrastructure.adapter;

import com.gooddaytaxi.dispatch.application.port.out.command.DispatchAssignmentCommandPort;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchAssignmentLog;
import com.gooddaytaxi.dispatch.domain.repository.DispatchAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DispatchAssignmentCommandAdapter implements DispatchAssignmentCommandPort {

    private final DispatchAssignmentRepository dispatchAssignmentRepository;

    @Override
    public DispatchAssignmentLog save(DispatchAssignmentLog dispatchAssignmentLog) {
        return dispatchAssignmentRepository.save(dispatchAssignmentLog);
    }
}
