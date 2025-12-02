package com.gooddaytaxi.dispatch.application.port.out.query;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DispatchQueryPort {
    List<Dispatch> findAllByFilter();
    Optional<Dispatch> findById(UUID dispatchId);
}
