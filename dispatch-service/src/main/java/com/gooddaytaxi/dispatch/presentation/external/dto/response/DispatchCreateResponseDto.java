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
public class DispatchCreateResponseDto {
    private UUID dispatchId;
    private UUID passengerId;
    private String pickupAddress;
    private String destinationAddress;
    private String dispatchStatus;
    private LocalDateTime requestCreatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
}