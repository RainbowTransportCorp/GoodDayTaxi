package com.gooddaytaxi.dispatch.domain.repository;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 도메인에서 필요한 조회 규칙을 정의하는 인터페이스.
 */
public interface DispatchRepositoryCustom {
    List<Dispatch> findAllByPassengerId(UUID passengerId);
    Optional<Dispatch> findByDispatchId(UUID id);
    List<Dispatch> findByDriverIdAndStatus(UUID driverId, DispatchStatus status);
    Optional<Dispatch> findByIdAndPassengerId(UUID dispatchId, UUID passengerId);
    List<Dispatch> findTimeoutCandidates();
    List<Dispatch> findByStatus(DispatchStatus status);
}
