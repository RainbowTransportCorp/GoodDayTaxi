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
public class DispatchSummaryResult {
    private final UUID dispatchId;
    private final String pickupAddress;
    private final String destinationAddress;
    private final DispatchStatus dispatchStatus;
    private final UUID driverId;
    private final LocalDateTime createdAt;
}
