package com.gooddaytaxi.dispatch.application.usecase.reject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DispatchRejectCommand {
    private UUID dispatchId;
    private UUID driverId;
    private String reason;
}
