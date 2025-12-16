package com.gooddaytaxi.dispatch.application.service.assignmentLog;

import com.gooddaytaxi.dispatch.domain.repository.DispatchAssignmentLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentLogQueryService {

    private final DispatchAssignmentLogRepository repository;

    public List<UUID> findPreviouslyTriedDrivers(UUID dispatchId) {
        return repository.findAllDriverIdsByDispatchId(dispatchId);
    }
}

