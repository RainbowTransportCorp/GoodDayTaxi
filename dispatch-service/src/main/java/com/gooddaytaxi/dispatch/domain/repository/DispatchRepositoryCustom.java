package com.gooddaytaxi.dispatch.domain.repository;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Dispatch 도메인에 대한 커스텀 조회 규칙 정의
 *
 * - JPA 기본 메서드로 표현하기 어려운 조회 조건을 분리한다
 * - 조회 전용 로직만 포함한다
 */
public interface DispatchRepositoryCustom {

    /**
     * 특정 승객의 배차 목록 조회
     */
    List<Dispatch> findAllByPassengerId(UUID passengerId);

    /**
     * 배차 식별자로 배차 단건 조회
     */
    Optional<Dispatch> findByDispatchId(UUID id);

    /**
     * 승객 소유 배차 단건 조회
     */
    Optional<Dispatch> findByIdAndPassengerId(UUID dispatchId, UUID passengerId);

    /**
     * 타임아웃 처리 대상 배차 조회
     */
    List<Dispatch> findTimeoutCandidates();

    /**
     * 상태 기준 배차 목록 조회
     */
    List<Dispatch> findByStatus(DispatchStatus status);
}

