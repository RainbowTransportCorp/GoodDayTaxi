package com.gooddaytaxi.payment.infrastructure.repository;

import com.gooddaytaxi.payment.domain.entity.RefundRequest;
import com.gooddaytaxi.payment.domain.enums.PaymentMethod;
import com.gooddaytaxi.payment.domain.enums.RefundRequestStatus;
import com.gooddaytaxi.payment.domain.repository.RefundRequestRepositoryCustom;
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
import static com.gooddaytaxi.payment.domain.entity.QRefundRequest.refundRequest;
import static com.gooddaytaxi.payment.domain.entity.QPayment.payment;

@Repository
@RequiredArgsConstructor
public class RefundRequestRepositoryImpl implements RefundRequestRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 검색 조건에 따른 환불 요청 조회
    @Override
    public Page<RefundRequest> searchRefundRequests(UUID paymentId, String status, String reasonKeyword, String method, UUID passeangerId, UUID driverId, LocalDateTime startDay, LocalDateTime endDay, Pageable pageable) {
        List<RefundRequest> requests = queryFactory
                .selectFrom(refundRequest)
                .join(payment).on(payment.id.eq(refundRequest.paymentId))
                .where(eqPaymentId(paymentId),
                       eqStatus(status),
                       likeReasonKeyword(reasonKeyword),
                       eqMethod(method),
                       eqPassengerId(passeangerId),
                       eqDriverId(driverId),
                       betweenCreatedAt(startDay, endDay))
                .orderBy(builderRefundRequestOrderSepifier(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        Long total = queryFactory
                .select(refundRequest.count())
                .from(refundRequest)
                .join(payment).on(payment.id.eq(refundRequest.paymentId))
                .where(eqPaymentId(paymentId),
                        eqStatus(status),
                        likeReasonKeyword(reasonKeyword),
                        eqMethod(method),
                        eqPassengerId(passeangerId),
                        eqDriverId(driverId),
                        betweenCreatedAt(startDay, endDay))
                .fetchOne();
        total = total != null ? total : 0L;
        return new PageImpl<>(requests, pageable, total);
    }

    //정렬조건 처리
    private OrderSpecifier<?>[] builderRefundRequestOrderSepifier(Pageable pageable) {
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
                                            .when(refundRequest.status.eq(RefundRequestStatus.REQUESTED))
                                            .then(0)
                                            .when(refundRequest.status.eq(RefundRequestStatus.APPROVED))
                                            .then(1)
                                            .when(refundRequest.status.eq(RefundRequestStatus.REJECTED))
                                            .then(2)
                                            .otherwise(99);  // COMPLETED
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
    private BooleanExpression eqPaymentId(UUID paymentId) {
        return (paymentId!=null) ? refundRequest.paymentId.eq(paymentId) : null;
    }
    private BooleanExpression eqStatus(String status) {
        return StringUtils.hasText(status) ? refundRequest.status.eq(RefundRequestStatus.of(status)) : null;
    }
    private BooleanExpression likeReasonKeyword(String reasonKeyword) {
        return StringUtils.hasText(reasonKeyword) ? refundRequest.reason.containsIgnoreCase(reasonKeyword) : null;
    }
    private BooleanExpression eqMethod(String method) {
        return StringUtils.hasText(method) ? payment.method.eq(PaymentMethod.of(method)) : null;
    }
    private BooleanExpression eqPassengerId(UUID passengerId) {
        return (passengerId!=null) ? payment.passengerId.eq(passengerId) : null;
    }
    private BooleanExpression eqDriverId(UUID driverId) {
        return (driverId!=null) ? payment.driverId.eq(driverId) : null;
    }
    private BooleanExpression betweenCreatedAt(LocalDateTime startDay, LocalDateTime endDay) {
        if (startDay != null && endDay != null) {
            return refundRequest.createdAt.between(startDay, endDay);
        } else if (startDay != null) {
            return refundRequest.createdAt.goe(startDay);
        } else if (endDay != null) {
            return refundRequest.createdAt.loe(endDay);
        } else {
            return null;
        }
    }
}
