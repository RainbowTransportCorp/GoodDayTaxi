package com.gooddaytaxi.dispatch.application.usecase.timeout;

import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class AdminForceTimeoutResult {

    private UUID dispatchId;
    private DispatchStatus status;
    private LocalDateTime timeoutAt;
}