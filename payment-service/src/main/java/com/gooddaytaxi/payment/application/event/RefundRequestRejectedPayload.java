package com.gooddaytaxi.payment.application.event;

import com.gooddaytaxi.payment.domain.repository.PaymentIdentityView;
import com.gooddaytaxi.payment.domain.entity.RefundRequest;

import java.time.LocalDateTime;
import java.util.UUID;

public record RefundRequestRejectedPayload(
        UUID notificationOriginId,
        UUID notifierId,   //이벤트 발동 유저 Id
        UUID paymentId,
        UUID passeangerId,
        UUID tripId,
        String rejectedReason,
        LocalDateTime rejectedAt
) {

    public static RefundRequestRejectedPayload from(PaymentIdentityView payment, RefundRequest request, UUID userId) {
        return new RefundRequestRejectedPayload(
                request.getId(),
                userId,
                payment.getId(),
                payment.getPassengerId(),
                payment.getTripId(),
                request.getResponse(),
                request.getRejectedAt()
        );
    }
}
