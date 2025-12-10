package com.gooddaytaxi.dispatch.application.usecase.reject;

import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DispatchRejectResult {
    private final UUID dispatchId;
    private final UUID driverId;
    private final DispatchStatus dispatchStatus;   // REJECTED
    private final LocalDateTime rejectedAt;
}

