package com.gooddaytaxi.dispatch.application.usecase.cancel;

import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DispatchCancelResult {
    private final UUID dispatchId;
    private final DispatchStatus dispatchStatus;
    private final LocalDateTime canceledAt;
}

