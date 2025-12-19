package com.gooddaytaxi.dispatch.application.port.out.query;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;

import java.util.List;
import java.util.UUID;

public interface DispatchAssignmentLogQueryPort {

    List<Dispatch> findAssigningByCandidateDriver(UUID driverId);

    List<UUID> findAllDriverIdsByDispatchId(UUID dispatchId);
}