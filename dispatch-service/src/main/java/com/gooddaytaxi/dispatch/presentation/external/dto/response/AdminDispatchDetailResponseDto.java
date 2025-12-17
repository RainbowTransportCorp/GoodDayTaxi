package com.gooddaytaxi.dispatch.presentation.external.dto.response;

import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class AdminDispatchDetailResponseDto {
    private final UUID dispatchId;
    private final UUID passengerId;
    private final UUID driverId;
    private final DispatchStatus status;
    private final String pickupAddress;
    private final String destinationAddress;
    private final int reassignCount;
    private final LocalDateTime assignedAt;
    private final LocalDateTime acceptedAt;
    private final LocalDateTime timeoutAt;
}