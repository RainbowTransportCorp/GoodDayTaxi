package com.gooddaytaxi.dispatch.presentation.external.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DispatchDetailResponseDto {

    private final UUID dispatchId;
    private final UUID passengerId;
    private final UUID driverId;
    private final String pickupAddress;
    private final String destinationAddress;
    private final String dispatchStatus;
    private final LocalDateTime requestCreatedAt;
    private final LocalDateTime assignedAt;
    private final LocalDateTime acceptedAt;
    private final LocalDateTime canceledAt;
    private final LocalDateTime timeoutAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}