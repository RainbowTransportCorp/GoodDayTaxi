package com.gooddaytaxi.dispatch.domain.repository;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 도메인에서 필요한 조회 규칙을 정의하는 인터페이스.
 *
 * 현재는 모든 조회가 Application → Port → Adapter 경유로 처리되지만,
 * 추후 도메인 또는 서비스 내부에서 직접 조회가 필요해질 가능성을 고려하여
 * 조회 의도(무엇을 조회하는가)는 도메인 레이어에서 명시적으로 표현한다.
 *
 * 실제 쿼리 구현(QueryDSL/JPA)은 인프라 레이어에서 담당한다.
 */
public interface DispatchRepositoryCustom {
    List<Dispatch> findAllByPassengerId(UUID passengerId);
    Optional<Dispatch> findByDispatchId(UUID id);
     List<Dispatch> findTimeoutTargets(int seconds);

    List<Dispatch> findByDriverIdAndStatus(UUID driverId, DispatchStatus status);

    Optional<Dispatch> findByIdAndPassengerId(UUID dispatchId, UUID passengerId);
}
