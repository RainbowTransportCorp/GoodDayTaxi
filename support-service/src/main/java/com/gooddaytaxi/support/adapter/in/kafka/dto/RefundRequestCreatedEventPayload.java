package com.gooddaytaxi.support.adapter.in.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RefundRequestCreatedEventPayload (
        UUID notificationOriginId,  // Refund Request ID
        UUID notifierId,            // Passenger ID
//        UUID dispatchId,
        UUID tripId,
        UUID paymentId,
        UUID driverId,
        UUID passengerId,
        Long amount,
        String method,
        String reason,
        LocalDateTime requestedAt
){ }
