package com.gooddaytaxi.support.application.port.out.persistence;

import com.gooddaytaxi.support.application.service.NotificationFilter;
import com.gooddaytaxi.support.domain.notification.model.Notification;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
* 조회 필터링 기준 명세
*/
@RequiredArgsConstructor
public final class NotificationSpecifications {

    public static Specification<Notification> applyFilter(NotificationFilter filter) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // soft delete 제외
            predicates.add(cb.isNull(root.get("deletedAt")));

            if (filter.notificationType() != null) {
                predicates.add(cb.equal(
                        root.get("notificationType"),
                        filter.notificationType()
                ));
            }

//            if (filter.dispatchId() != null) {
//                predicates.add(cb.equal(root.get("dispatchId"), filter.dispatchId()));
//            }
//
//            if (filter.tripId() != null) {
//                predicates.add(cb.equal(root.get("tripId"), filter.tripId()));
//            }
//
//            if (filter.paymentId() != null) {
//                predicates.add(cb.equal(root.get("paymentId"), filter.paymentId()));
//            }

            if (filter.from() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("notifiedAt"),
                        filter.from()
                ));
            }

            if (filter.to() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("notifiedAt"),
                        filter.to()
                ));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}

