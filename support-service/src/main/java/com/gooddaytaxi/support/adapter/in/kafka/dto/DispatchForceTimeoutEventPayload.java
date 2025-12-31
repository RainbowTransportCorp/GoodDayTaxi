package com.gooddaytaxi.support.adapter.in.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DISPATCH_FORCE_TIMEOUT 이벤트 Payload DTO
 */
public record DispatchForceTimeoutEventPayload(
    UUID notificationOriginId,
    UUID notifierId,
    UUID dispatchId,
    UUID driverId,
    String previousStatus,
    LocalDateTime forceTimeoutAt,
    String reason,
    String message,
    boolean tripRequestMayHaveBeenSent
) {}