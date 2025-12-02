package com.gooddaytaxi.dispatch.domain.repository;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DispatchRepositoryCustom {
    List<Dispatch> findAllByCondition();

    Optional<Dispatch> findByDispatchId(UUID id);
}
