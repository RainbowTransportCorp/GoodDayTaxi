package com.gooddaytaxi.payment.application.event;

import com.gooddaytaxi.payment.domain.entity.Payment;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentCompletePayload(
        UUID paymentId,
        UUID passeangerId,
        UUID driverId,
        UUID tripId,
        Long amount,
        String method,
        LocalDateTime approvedAt
){

    public static PaymentCompletePayload from(Payment payment) {
        return new PaymentCompletePayload(
                payment.getId(),
                payment.getPassengerId(),
                payment.getDriverId(),
                payment.getTripId(),
                payment.getAmount().value(),
                payment.getMethod().name(),
                payment.getApprovedAt()
        );
    }
}
