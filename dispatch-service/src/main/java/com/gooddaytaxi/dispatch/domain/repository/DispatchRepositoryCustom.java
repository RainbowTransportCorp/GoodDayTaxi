package com.gooddaytaxi.dispatch.domain.repository;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.entity.QDispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DispatchRepositoryCustom {
    List<Dispatch> findAllByCondition(UUID passengerId);
    List<Dispatch> findByStatus(DispatchStatus status);
    Optional<Dispatch> findByDispatchId(UUID id);
     List<Dispatch> findTimeoutTargets(int seconds);
}
