package com.gooddaytaxi.payment.application.event;

import com.gooddaytaxi.payment.domain.entity.Payment;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentCompletePayload(
        UUID notificationOriginId,
        UUID notifierId,   //이벤트 발동 유저 Id
        UUID passengerId,
        UUID driverId,
        UUID tripId,
        Long amount,
        String method,
        LocalDateTime approvedAt
){

    public static PaymentCompletePayload from(Payment payment, UUID notifierId) {
        return new PaymentCompletePayload(
                payment.getId(),
                notifierId,
                payment.getPassengerId(),
                payment.getDriverId(),
                payment.getTripId(),
                payment.getAmount().value(),
                payment.getMethod().name(),
                payment.getApprovedAt()
        );
    }
}
