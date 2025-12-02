package com.gooddaytaxi.dispatch.presentation.external.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class DispatchDetailResponseDto {

    private UUID dispatchId;
    private UUID passengerId;
    private UUID driverId;
    private String pickupAddress;
    private String destinationAddress;
    private String dispatchStatus;
    private LocalDateTime requestCreatedAt;
    private LocalDateTime assignedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime timeoutAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}