package com.gooddaytaxi.dispatch.presentation.external.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DispatchPendingListResponseDto {
    private final String dispatchId;
    private final String pickupAddress;
    private final String destinationAddress;
    private final String dispatchStatus;
    private final String requestCreatedAt;
}
