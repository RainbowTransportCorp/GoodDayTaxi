package com.gooddaytaxi.dispatch.application.port.out.query;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;

import java.util.List;
import java.util.UUID;

public interface DispatchQueryPort {
    List<Dispatch> findAllByPassengerId(UUID passengerId);
    Dispatch findById(UUID dispatchId);
    List<Dispatch> findByStatus(DispatchStatus status);
    List<Dispatch> findTimeoutTargets(int seconds);
}
