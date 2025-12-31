package com.gooddaytaxi.dispatch.application.port.out.query;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DispatchQueryPort {

    List<Dispatch> findAll();

    List<Dispatch> findByStatus(DispatchStatus status);

    List<Dispatch> findAllByPassengerId(UUID passengerId);

    Dispatch findById(UUID dispatchId);

    List<Dispatch> findTimeoutCandidates();

    Optional<Dispatch> findByIdAndPassengerId(
            UUID dispatchId,
            UUID passengerId
    );
}

