package com.gooddaytaxi.support.adapter.out.persistence;

import com.gooddaytaxi.support.domain.log.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * 로그 Spring Data JPA 리포지토리
 */
public interface LogJpaRepository extends JpaRepository<Log, UUID> {
    /**
     * 로그 생성, 삭제
     */
    Log save(Log log);
}
