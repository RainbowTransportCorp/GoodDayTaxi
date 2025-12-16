package com.gooddaytaxi.dispatch.application.usecase.admin;

import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class AdminDispatchListResult {

    private final UUID dispatchId;
    private final UUID passengerId;
    private final UUID driverId;
    private final DispatchStatus status;
    private final int reassignCount;
    private final LocalDateTime requestedAt;
    private final LocalDateTime updatedAt;
}