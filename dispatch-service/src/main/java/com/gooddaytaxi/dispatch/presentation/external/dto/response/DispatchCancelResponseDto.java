package com.gooddaytaxi.dispatch.presentation.external.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DispatchCancelResponseDto {
    private UUID dispatchId;
    private String dispatchStatus;
    private LocalDateTime cancelledAt;
}

