package com.gooddaytaxi.support.adapter.in.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentCompletedEventPayload (
        UUID notificationOriginId,
        UUID notifierId,
//        UUID dispatchId,
        UUID tripId,
        UUID driverId,
        UUID passengerId,
        Long amount,
        String method,
        LocalDateTime approvedAt
){}
