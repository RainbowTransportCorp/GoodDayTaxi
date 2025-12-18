package com.gooddaytaxi.support.adapter.out.persistence;

import com.gooddaytaxi.support.application.port.out.persistence.LogQueryPersistencePort;
import com.gooddaytaxi.support.application.port.out.persistence.LogSpecifications;
import com.gooddaytaxi.support.application.service.AdminLogFilter;
import com.gooddaytaxi.support.domain.log.model.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * JPA 기반 로그 리포지토리 구현체
 */
@RequiredArgsConstructor
@Component
public class LogQueryPersistenceAdapter implements LogQueryPersistencePort {

    private final LogJpaRepository logJpaRepository;

    /**
     * 로그 조회
     */
    @Override
    public Page<Log> search(AdminLogFilter filter, Pageable pageable) {
        return logJpaRepository.findAll(LogSpecifications.applyFilter(filter), pageable);
    }
}
