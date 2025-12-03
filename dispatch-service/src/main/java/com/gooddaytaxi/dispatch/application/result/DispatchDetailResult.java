package com.gooddaytaxi.dispatch.application.result;

import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DispatchDetailResult {
    private final UUID dispatchId;
    private final UUID passengerId;
    private final UUID driverId;
    private final String pickupAddress;
    private final String destinationAddress;
    private final DispatchStatus dispatchStatus;
    private final LocalDateTime requestCreatedAt;
    private final LocalDateTime assignedAt;
    private final LocalDateTime acceptedAt;
    private final LocalDateTime cancelledAt;
    private final LocalDateTime timeoutAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}