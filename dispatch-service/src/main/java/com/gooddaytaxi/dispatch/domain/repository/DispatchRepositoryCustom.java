package com.gooddaytaxi.dispatch.domain.repository;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 도메인에서 필요한 조회 규칙을 정의하는 인터페이스.
 *
 * - "무엇을 조회해야 하는지"에 대한 도메인 관점의 요구사항만 선언한다.
 * - 실제 구현(QueryDSL/JPA)은 인프라 레이어에서 담당한다.
 * - 애플리케이션 서비스는 이 인터페이스를 직접 쓰지 않고,
 *   별도의 QueryPort(예: DispatchQueryPort)를 통해 간접적으로 사용한다.
 *
 * 즉, 도메인은 조회 의도를 정의하고,
 * 애플리케이션은 Port에 의존하며,
 * 인프라가 이 인터페이스를 구현하여 DB 접근을 담당한다.
 */
public interface DispatchRepositoryCustom {
    List<Dispatch> findAllByPassengerId(UUID passengerId);
    Optional<Dispatch> findByDispatchId(UUID id);
    List<Dispatch> findByDriverIdAndStatus(UUID driverId, DispatchStatus status);
    Optional<Dispatch> findByIdAndPassengerId(UUID dispatchId, UUID passengerId);
    List<Dispatch> findTimeoutCandidates();
    List<Dispatch> findByStatus(DispatchStatus status);
}
