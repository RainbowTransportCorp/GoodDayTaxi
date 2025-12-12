package com.gooddaytaxi.dispatch.application.service.assignmentLog;

import com.gooddaytaxi.dispatch.domain.repository.DispatchAssignmentLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentLogQueryService {

    private final DispatchAssignmentLogRepository repository;

    public List<UUID> findPreviouslyTriedDrivers(UUID dispatchId) {
        return repository.findAllDriverIdsByDispatchId(dispatchId);
    }
}

