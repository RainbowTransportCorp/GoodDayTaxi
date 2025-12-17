package com.gooddaytaxi.support.adapter.in.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RefundRequestRejectedEventPayload(
        UUID notificationOriginId, // Refund Request ID
        UUID notifierId,           // ADMIN_MASTER ID
//        UUID dispatchId,
        UUID tripId,
        UUID paymentId,
        UUID passengerId,
        String rejectReason,
        LocalDateTime rejectedAt
){ }
