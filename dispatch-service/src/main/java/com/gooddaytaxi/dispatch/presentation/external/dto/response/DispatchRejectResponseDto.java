package com.gooddaytaxi.dispatch.presentation.external.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DispatchRejectResponseDto {
    private final UUID dispatchId;
    private final Long driverId;
    private final String dispatchStatus;
    private final LocalDateTime rejectedAt;
}
