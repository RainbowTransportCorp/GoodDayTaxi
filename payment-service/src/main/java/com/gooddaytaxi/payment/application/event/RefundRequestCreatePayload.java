package com.gooddaytaxi.payment.application.event;

import com.gooddaytaxi.payment.domain.entity.Payment;
import com.gooddaytaxi.payment.domain.entity.RefundRequest;

import java.time.LocalDateTime;
import java.util.UUID;

public record RefundRequestCreatePayload (
        UUID notificationOriginId,
        UUID notifierId,
        Long amount,
        String method,
        UUID paymentId,
        UUID passeangerId,
        UUID driverId,
        UUID tripId,
        String reason,
        LocalDateTime requestedAt
){

    public static RefundRequestCreatePayload from (Payment payment, RefundRequest refundRequest) {
        return new RefundRequestCreatePayload(
                refundRequest.getId(),
                payment.getPassengerId(),
                payment.getAmount().value(),
                payment.getMethod().name(),
                payment.getId(),
                payment.getPassengerId(),
                payment.getDriverId(),
                payment.getTripId(),
                refundRequest.getReason(),
                refundRequest.getRequestedAt()
        );
    }
}
