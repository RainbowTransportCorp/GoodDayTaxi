package com.gooddaytaxi.payment.infrastructure.repository;

import com.gooddaytaxi.payment.domain.entity.Payment;
import com.gooddaytaxi.payment.domain.enums.PaymentMethod;
import com.gooddaytaxi.payment.domain.enums.PaymentStatus;
import com.gooddaytaxi.payment.domain.repository.PaymentRepositoryCustom;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.gooddaytaxi.payment.domain.entity.QPayment.payment;


@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Payment> searchPayments(String method, String status, UUID passeangerId, UUID driverId, UUID tripId, LocalDateTime startDay, LocalDateTime endDay, Pageable pageable) {
        List<Payment> payments = queryFactory
                .selectFrom(payment)
                .where(
                        eqMethod(method),
                        eqStatus(status),
                        eqPassengerId(passeangerId),
                        eqDriverId(driverId),
                        eqTripId(tripId),
                        betweenCreatedAt(startDay, endDay)
                )
                .orderBy(builderPaymentOrderSepifier(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        Long total = queryFactory
                .select(payment.count())
                .from(payment)
                .where(
                        eqMethod(method),
                        eqStatus(status),
                        eqPassengerId(passeangerId),
                        eqDriverId(driverId),
                        eqTripId(tripId),
                        betweenCreatedAt(startDay, endDay)
                )
                .fetchOne();
        total = total != null ? total : 0L; // 결과개수가 0이면 null이 반환되므로 0으로 처리
        return new PageImpl<>(payments, pageable, total);
    }



    private OrderSpecifier<?>[] builderPaymentOrderSepifier(Pageable pageable) {
        List<OrderSpecifier<?>> specs = new ArrayList<>();
        pageable.getSort().forEach(
                order -> {
                    OrderSpecifier<?> orderSpecifier;
                    String property = order.getProperty();
                    boolean isAscending = order.isAscending();
                    switch (property) {
                        case "method" -> {
                            NumberExpression<Integer> methodPriority =
                                    new CaseBuilder()
                                            .when(payment.method.eq(PaymentMethod.CASH))
                                            .then(0)
                                            .when(payment.method.eq(PaymentMethod.CARD))
                                            .then(1)
                                            .when(payment.method.eq(PaymentMethod.TOSS_PAY))
                                            .then(2)
                                            .otherwise(99);
                            orderSpecifier = isAscending ? methodPriority.asc() : methodPriority.desc();
                        }
                        case "status" -> {
                            NumberExpression<Integer> statusPriority =
                                    new CaseBuilder()
                                            .when(payment.status.eq(PaymentStatus.PENDING))
                                            .then(0)
                                            .when(payment.status.eq(PaymentStatus.COMPLETED))
                                            .then(1)
                                            .when(payment.status.eq(PaymentStatus.FAILED))
                                            .then(2)
                                            .otherwise(99);
                            orderSpecifier = isAscending ? statusPriority.asc() : statusPriority.desc();
                        }
                        case "createdAt" -> orderSpecifier = isAscending ? payment.createdAt.asc() : payment.createdAt.desc();
                        //정렬조건은 이미 체크했으니 디폴트는 updatedAt으로 처리
                        default -> orderSpecifier = isAscending ? payment.updatedAt.asc() : payment.updatedAt.desc();

                    }
                    specs.add(orderSpecifier);
                });
        return specs.toArray(new OrderSpecifier<?>[0]);
    }


    // 검색 조건 null 처리
    private BooleanExpression eqMethod(String method) {
        return StringUtils.hasText(method) ? payment.method.eq(PaymentMethod.of(method)) : null;
    }
    private BooleanExpression eqStatus(String status) {
        return StringUtils.hasText(status) ? payment.status.eq(PaymentStatus.of(status)) : null;
    }
    private BooleanExpression eqPassengerId(UUID passengerId) {
        return (passengerId!=null) ? payment.passengerId.eq(passengerId) : null;
    }
    private BooleanExpression eqDriverId(UUID driverId) {
        return (driverId!=null) ? payment.driverId.eq(driverId) : null;
    }
    private BooleanExpression eqTripId(UUID tripId) {
        return (tripId!=null) ? payment.tripId.eq(tripId) : null;
    }
    private BooleanExpression betweenCreatedAt(LocalDateTime startDay, LocalDateTime endDay) {
        if (startDay != null && endDay != null) {
            return payment.createdAt.between(startDay, endDay);
        } else if (startDay != null) {
            return payment.createdAt.goe(startDay);
        } else if (endDay != null) {
            return payment.createdAt.loe(endDay);
        } else {
            return null;
        }
    }
}
