package com.gooddaytaxi.dispatch.application.result;

import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class DispatchDetailResult {

    private UUID dispatchId;
    private UUID passengerId;
    private UUID driverId;
    private String pickupAddress;
    private String destinationAddress;
    private DispatchStatus dispatchStatus;
    private LocalDateTime requestCreatedAt;
    private LocalDateTime assignedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime timeoutAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}