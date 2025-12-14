package com.gooddaytaxi.payment.application.event;

import com.gooddaytaxi.payment.domain.entity.Payment;
import com.gooddaytaxi.payment.domain.entity.Refund;
import com.gooddaytaxi.payment.domain.enums.PaymentMethod;

import java.time.LocalDateTime;
import java.util.UUID;

public record RefundCompletedPayload(
        UUID notificationOriginId,
        UUID notifierId,   //이벤트 발동 유저 Id
        Long amount,
        String method,
        String pgProvider,
        UUID paymentId,
        UUID passeangerId,
        UUID driverId,
        UUID tripId,
        String reason,
        String message,
        LocalDateTime approvedAt,
        LocalDateTime refundedAt
) {
    public static RefundCompletedPayload from(Payment payment, Refund refund, UUID notifierId) {
        return new RefundCompletedPayload(
                refund.getId(),
                notifierId,
                payment.getAmount().value(),
                payment.getMethod().name(),
                payment.getMethod() == PaymentMethod.TOSS_PAY? payment.getAttempts().get(0).getPgProvider() : null,
                payment.getId(),
                payment.getPassengerId(),
                payment.getDriverId(),
                payment.getTripId(),
                refund.getReason().name(),
                refund.getDetailReason(),
                payment.getApprovedAt(),
                refund.getRefundedAt()
        );

    }
}
