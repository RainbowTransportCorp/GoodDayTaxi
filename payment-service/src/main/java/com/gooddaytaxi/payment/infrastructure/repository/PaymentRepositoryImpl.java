package com.gooddaytaxi.payment.infrastructure.repository;

import com.gooddaytaxi.payment.domain.entity.Payment;
import com.gooddaytaxi.payment.domain.entity.PaymentAttempt;
import com.gooddaytaxi.payment.domain.entity.QPaymentAttempt;
import com.gooddaytaxi.payment.domain.entity.Refund;
import com.gooddaytaxi.payment.domain.enums.PaymentMethod;
import com.gooddaytaxi.payment.domain.enums.PaymentStatus;
import com.gooddaytaxi.payment.domain.enums.RefundReason;
import com.gooddaytaxi.payment.domain.enums.RefundStatus;
import com.gooddaytaxi.payment.domain.repository.PaymentRepositoryCustom;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
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
import java.util.Optional;
import java.util.UUID;

import static com.gooddaytaxi.payment.domain.entity.QPayment.payment;
import static com.gooddaytaxi.payment.domain.entity.QRefund.refund;


@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<PaymentAttempt> findFirstByPaymentIdOrderByAttemptNoDesc(UUID paymentId) {
        QPaymentAttempt a = QPaymentAttempt.paymentAttempt;
        return Optional.ofNullable(
                queryFactory.selectFrom(a)
                        .where(a.payment.id.eq(paymentId))
                        .orderBy(a.attemptNo.desc())
                        .limit(1)
                        .fetchOne()
        );
    }

    //결제 검색
    @Override
    public Page<Payment> searchPayments(String method, String status, UUID passeangerId, UUID driverId, UUID tripId, LocalDateTime startDay, LocalDateTime endDay, Pageable pageable) {
        List<Payment> payments = queryFactory
                .selectFrom(payment)
                .where(
                        eqPaymentMethod(method),
                        eqPaymentStatus(status),
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
                        eqPaymentMethod(method),
                        eqPaymentStatus(status),
                        eqPassengerId(passeangerId),
                        eqDriverId(driverId),
                        eqTripId(tripId),
                        betweenCreatedAt(startDay, endDay)
                )
                .fetchOne();
        total = total != null ? total : 0L; // 결과개수가 0이면 null이 반환되므로 0으로 처리
        return new PageImpl<>(payments, pageable, total);
    }

    //환불 검색
    @Override
    public Page<Refund> searchRefunds(String status, String reason, Boolean existRequest, UUID passeangerId, UUID driverId, UUID tripId, String method, Long minAmount, Long maxAmount, LocalDateTime startDay, LocalDateTime endDay, Pageable pageable) {
        List<Refund> refunds = queryFactory
                .selectFrom(refund)
                .innerJoin(refund.payment, payment)
                .where(
                        eqRefundStatus(status),
                        eqRefundReason(reason),
                        existRequest(existRequest),
                        eqPassengerId(passeangerId),
                        eqDriverId(driverId),
                        eqTripId(tripId),
                        eqPaymentMethod(method),
                        betweenAmount(minAmount, maxAmount),
                        betweenCanceledAt(startDay, endDay)
                )
                .orderBy(builderRefundOrderSepifier(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        Long total = queryFactory
                .select(refund.count())
                .from(refund)
                .innerJoin(refund.payment, payment)
                .where(
                        eqRefundStatus(status),
                        eqRefundReason(reason),
                        existRequest(existRequest),
                        eqPassengerId(passeangerId),
                        eqDriverId(driverId),
                        eqTripId(tripId),
                        eqPaymentMethod(method),
                        betweenAmount(minAmount, maxAmount),
                        betweenCanceledAt(startDay, endDay)
                )
                .fetchOne();
        total = total != null ? total : 0L; // 결과개수가 0이면 null이 반환되므로 0으로 처리
        return new PageImpl<>(refunds, pageable, total);
    }


    //결제 정렬조건 빌더
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

    //환불 정룔 조건 빌더
    private OrderSpecifier<?>[] builderRefundOrderSepifier(Pageable pageable) {
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
                                            .when(refund.status.eq(RefundStatus.SUCCESS))
                                            .then(0)
                                            .otherwise(99);
                            orderSpecifier = isAscending ? statusPriority.asc() : statusPriority.desc();
                        }
                        case "reason" -> {
                            NumberExpression<Integer> statusPriority =
                                    new CaseBuilder()
                                            .when(refund.reason.eq(RefundReason.CUSTOMER_REQUEST))
                                            .then(0)
                                            .when(refund.reason.eq(RefundReason.COMPANY_FAULT_SYSTEM))
                                            .then(1)
                                            .when(refund.reason.eq(RefundReason.DUPLICATE_PAYMENT))
                                            .then(2)
                                            .when(refund.reason.eq(RefundReason.PROMOTION_COMPENSATION))
                                            .then(3)
                                            .when(refund.reason.eq(RefundReason.ADMIN_ADJUSTMENT))
                                            .then(4)
                                            .otherwise(99);
                            orderSpecifier = isAscending ? statusPriority.asc() : statusPriority.desc();
                        }
                        //정렬조건은 이미 체크했으니 디폴트는 canceledAt으로 처리
                        default -> orderSpecifier = isAscending ? refund.canceledAt.asc() : refund.canceledAt.desc();

                    }
                    specs.add(orderSpecifier);
                });
        return specs.toArray(new OrderSpecifier<?>[0]);
    }


    // 검색 조건 null 처리
    private BooleanExpression eqPaymentMethod(String method) {
        return StringUtils.hasText(method) ? payment.method.eq(PaymentMethod.of(method)) : null;
    }
    //결제 상태
    private BooleanExpression eqPaymentStatus(String status) {
        return StringUtils.hasText(status) ? payment.status.eq(PaymentStatus.of(status)) : null;
    }
    //환불 상태
    private BooleanExpression eqRefundStatus(String status) {
        return StringUtils.hasText(status) ? refund.status.eq(RefundStatus.of(status)) : null;
    }
    //환불 이유
    private BooleanExpression eqRefundReason(String reason) {
        return StringUtils.hasText(reason) ? refund.reason.eq(RefundReason.of(reason)) : null;
    }
    //환불 요청 여부
    private BooleanExpression existRequest(Boolean existRequest) {
        if(existRequest == null) return null;
        else if(existRequest) {
            return refund.requestId.isNotNull();
        } else {
            return refund.requestId.isNull();
        }
    }
    //결제 승객 아이디
    private BooleanExpression eqPassengerId(UUID passengerId) {
        return (passengerId!=null) ? payment.passengerId.eq(passengerId) : null;
    }
    //결제 기사 아이디
    private BooleanExpression eqDriverId(UUID driverId) {
        return (driverId!=null) ? payment.driverId.eq(driverId) : null;
    }
    //결제 여행 아이디
    private BooleanExpression eqTripId(UUID tripId) {
        return (tripId!=null) ? payment.tripId.eq(tripId) : null;
    }
    //결제의 생성 날짜 범위 조건
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

    //환불의 승인 날짜 범위 조건
    private BooleanExpression betweenCanceledAt(LocalDateTime startDay, LocalDateTime endDay) {
        if (startDay != null && endDay != null) {
            return refund.canceledAt.between(startDay, endDay);
        } else if (startDay != null) {
            return refund.canceledAt.goe(startDay);
        } else if (endDay != null) {
            return refund.canceledAt.loe(endDay);
        } else {
            return null;
        }
    }

    //금액 범위 조건
    private BooleanExpression betweenAmount(Long minAmount, Long maxAmount) {
        //금액 Fare로 변환
        NumberPath<Long> amountValue = Expressions.numberPath(Long.class, payment, "amount");

        if (minAmount != null && maxAmount != null) {
            return amountValue.between(minAmount, maxAmount);
        } else if (minAmount != null) {
            return amountValue.goe(minAmount);
        } else if (maxAmount != null) {
            return amountValue.loe(maxAmount);
        } else {
            return null;
        }
    }
}
