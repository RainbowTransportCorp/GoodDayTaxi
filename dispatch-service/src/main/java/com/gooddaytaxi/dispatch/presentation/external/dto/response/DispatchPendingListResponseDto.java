package com.gooddaytaxi.dispatch.presentation.external.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class DispatchPendingListResponseDto {
    private final UUID dispatchId;
    private final String pickupAddress;
    private final String destinationAddress;
    private final String dispatchStatus;
    private final LocalDateTime requestCreatedAt;
}
