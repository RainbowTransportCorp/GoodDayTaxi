package com.gooddaytaxi.support.adapter.in.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DISPATCH_TIMEOUT 이벤트 Payload DTO
 */
public record DispatchTimeoutEventPayload(
    UUID notificationOriginId,
    UUID notifierId,
    UUID dispatchId,
    UUID passengerId,
    String message,
    LocalDateTime timeoutAt
) {}
