package com.gooddaytaxi.support.adapter.in.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RefundCompletedEventPayload(
    UUID notificationOriginId, // Refund Request ID
    UUID notifierId,           // ADMIN_MASTER ID
//    UUID dispatchId,
    UUID tripId,
    UUID paymentId,
    UUID driverId,
    UUID passengerId,
    String method,
    Long amount,
    String pgProvider,
    String reason,             // RefundReason Enum
    String message,
    LocalDateTime approvedAt,
    LocalDateTime refundedAt
) {}