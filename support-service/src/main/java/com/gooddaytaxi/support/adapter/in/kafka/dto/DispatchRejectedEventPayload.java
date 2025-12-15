package com.gooddaytaxi.support.adapter.in.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DISPATCH_REJECTED 이벤트 Payload DTO
 */
public record DispatchRejectedEventPayload(
        UUID dispatchId,
        UUID driverId,
        UUID passengerId,
        LocalDateTime rejectedAt
) {
}
