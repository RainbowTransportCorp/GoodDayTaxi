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
public class AdminForceTimeoutResponseDto {
    private UUID dispatchId;
    private DispatchStatus status;
    private LocalDateTime timeoutAt;
}
