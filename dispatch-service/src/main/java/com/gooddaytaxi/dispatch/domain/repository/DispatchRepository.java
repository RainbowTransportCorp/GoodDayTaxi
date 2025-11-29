package com.gooddaytaxi.dispatch.domain.repository;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;

import java.util.Optional;
import java.util.UUID;

public interface DispatchRepository {

    Dispatch save(Dispatch request);
    Optional<Dispatch> findById(UUID id);
}

