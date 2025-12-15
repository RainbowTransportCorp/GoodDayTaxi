package com.gooddaytaxi.dispatch.domain.repository;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchAssignmentLog;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DispatchAssignmentLogRepositoryCustom {
    Optional<DispatchAssignmentLog> findLatest(UUID dispatchId, UUID driverId);

    List<Dispatch> findPendingByCandidateDriver(UUID driverId);
    List<UUID> findAllDriverIdsByDispatchId(UUID dispatchId);
}
