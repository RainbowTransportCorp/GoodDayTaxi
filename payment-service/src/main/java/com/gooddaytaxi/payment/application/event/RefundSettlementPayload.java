package com.gooddaytaxi.payment.application.event;

import com.gooddaytaxi.payment.domain.entity.Payment;
import com.gooddaytaxi.payment.domain.enums.RefundReason;

import java.time.LocalDateTime;
import java.util.UUID;

public record RefundSettlementPayload(
        UUID notificationOriginId,
        UUID notifierId,   //이벤트 발동 유저 Id
        UUID paymentId,
        UUID driverId,
        Long amount,
        String method,
        String reason,
        LocalDateTime approvedAt
) {

    public static RefundSettlementPayload from(Payment payment, RefundReason reason, UUID notifierId) {
        return new RefundSettlementPayload(
                payment.getId(),
                notifierId,
                payment.getId(),
                payment.getDriverId(),
                payment.getAmount().value(),
                payment.getMethod().name(),
                reason.getDescription(),
                payment.getApprovedAt()
        );
    }
}
