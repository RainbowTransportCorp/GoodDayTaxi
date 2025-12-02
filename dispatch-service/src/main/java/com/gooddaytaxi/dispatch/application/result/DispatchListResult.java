package com.gooddaytaxi.dispatch.application.result;

import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class DispatchListResult {
    private UUID dispatchId;
    private String pickupAddress;
    private String destinationAddress;
    private DispatchStatus dispatchStatus;
    private UUID driverId;
    private LocalDateTime CreatedAt;
}
