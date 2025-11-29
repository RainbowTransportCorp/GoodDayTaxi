package com.gooddaytaxi.dispatch.application.port.out.query;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;

import java.util.Optional;
import java.util.UUID;

public interface DispatchQueryPort {

    Optional<Dispatch> findById(UUID dispatchId);
}
