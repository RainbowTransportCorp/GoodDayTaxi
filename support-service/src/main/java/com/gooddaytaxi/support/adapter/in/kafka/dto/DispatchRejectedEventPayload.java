package com.gooddaytaxi.support.adapter.in.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record DispatchRejectedEventPayload(
        UUID dispatchId,
        UUID driverId,
        LocalDateTime rejectedAt
) {
}
