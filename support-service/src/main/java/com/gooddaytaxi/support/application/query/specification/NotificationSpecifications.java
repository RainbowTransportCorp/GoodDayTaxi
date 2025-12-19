package com.gooddaytaxi.support.application.query.specification;

import com.gooddaytaxi.support.application.query.filter.AdminNotificationFilter;
import com.gooddaytaxi.support.domain.notification.model.Notification;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Notification 조회 필터 Specification
 * - 사용자 알림 조회
 * - 도메인 흐름 기반 알림 조회
 */
public final class NotificationSpecifications {

    private NotificationSpecifications() {}

    public static Specification<Notification> applyFilter(AdminNotificationFilter filter) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // 1. soft delete 제외
            predicates.add(cb.isNull(root.get("deletedAt")));

            // 2. 알림 수신자 기준
            if (filter.notifierId() != null) {
                predicates.add(
                        cb.equal(root.get("notifierId"), filter.notifierId())
                );
            }

            // 3. 특정 도메인 흐름(콜/운행/결제) 기준
            if (filter.notificationOriginId() != null) {
                predicates.add(
                        cb.equal(root.get("notificationOriginId"), filter.notificationOriginId())
                );
            }

            // 4. 알림 타입
            if (filter.notificationType() != null) {
                predicates.add(
                        cb.equal(root.get("notificationType"), filter.notificationType())
                );
            }

            // 5. 날짜 범위 - 이후
            if (filter.from() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("notifiedAt"), filter.from())
                );
            }
            // 5. 날짜 범위 - 이전
            if (filter.to() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("notifiedAt"), filter.to())
                );
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
