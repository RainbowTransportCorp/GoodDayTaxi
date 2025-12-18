package com.gooddaytaxi.support.application.port.out.persistence;

import com.gooddaytaxi.support.application.service.AdminLogFilter;
import com.gooddaytaxi.support.domain.log.model.Log;
import com.gooddaytaxi.support.domain.log.model.LogType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Log 조회 필터 Specification
 * - 관리자 시스템 로그 조회
 */
public final class LogSpecifications {

    private LogSpecifications() {}

    public static Specification<Log> applyFilter(AdminLogFilter filter) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // 1. soft delete 제외
            predicates.add(cb.isNull(root.get("deletedAt")));

            // 2. 로그 타입
            if (filter.logType() != null && !filter.logType().isBlank()) {
                predicates.add(
                        cb.equal(
                                root.get("logType"),
                                LogType.valueOf(filter.logType())
                        )
                );
            }

            // 3. 날짜 범위 - 이후
            if (filter.from() != null && !filter.from().isBlank()) {
                LocalDateTime from = LocalDateTime.parse(filter.from());
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("occurredAt"), from)
                );
            }

            // 4. 날짜 범위 - 이전
            if (filter.to() != null && !filter.to().isBlank()) {
                LocalDateTime to = LocalDateTime.parse(filter.to());
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("occurredAt"), to)
                );
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
