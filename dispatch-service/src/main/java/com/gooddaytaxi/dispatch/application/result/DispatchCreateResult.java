package com.gooddaytaxi.dispatch.application.result;

import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class DispatchCreateResult {
    private UUID dispatchId;
    private Long passengerId;
    private String pickupAddress;
    private String destinationAddress;
    private DispatchStatus dispatchStatus;
    private LocalDateTime requestCreatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}