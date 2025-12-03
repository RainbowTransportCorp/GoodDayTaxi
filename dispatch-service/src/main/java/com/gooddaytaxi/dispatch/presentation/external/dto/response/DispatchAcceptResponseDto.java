package com.gooddaytaxi.dispatch.presentation.external.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DispatchAcceptResponseDto {
    private final UUID dispatchId;
    private final UUID driverId;
    private final String dispatchStatus;   // ACCEPTED
    private final LocalDateTime acceptedAt;
}
