package com.gooddaytaxi.support.adapter.in.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RefundSettlementCreatedEventPayload(
        UUID notificationOriginId, // Payment ID
        UUID notifierId,           // ADMIN_MASTER ID
//    UUID dispatchId,
        UUID tripId,
        UUID paymentId,
        UUID driverId,
        String method,
        Long amount,
        String reason,
        LocalDateTime approvedAt
) {}
