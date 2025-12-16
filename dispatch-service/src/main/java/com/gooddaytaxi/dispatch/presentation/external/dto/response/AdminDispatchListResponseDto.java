package com.gooddaytaxi.dispatch.presentation.external.dto.response;

import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
public class AdminDispatchListResponseDto {
    UUID dispatchId;
    UUID passengerId;
    UUID driverId;
    DispatchStatus status;
    int reassignCount;
    LocalDateTime requestedAt;
    LocalDateTime updatedAt;
}
