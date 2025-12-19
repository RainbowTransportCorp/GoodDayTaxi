package com.gooddaytaxi.support.adapter.out.persistence;

import com.gooddaytaxi.support.application.port.out.persistence.LogCommandPersistencePort;
import com.gooddaytaxi.support.domain.log.model.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * JPA 기반 로그 리포지토리 구현체
 */
@RequiredArgsConstructor
@Component
public class LogCommandPersistenceAdapter implements LogCommandPersistencePort {
    private final LogJpaRepository logJpaRepository;

    /**
     * 로그 저장, 삭제
     */
    @Override
    public Log save(Log log) {
        return logJpaRepository.save(log);
    }
}
