package com.gooddaytaxi.dispatch.presentation.external.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DispatchListResponseDto {
    private UUID dispatchId;
    private String pickupAddress;
    private String destinationAddress;
    private String dispatchStatus;
    private UUID driverId;
    private LocalDateTime requestCreatedAt;
}

