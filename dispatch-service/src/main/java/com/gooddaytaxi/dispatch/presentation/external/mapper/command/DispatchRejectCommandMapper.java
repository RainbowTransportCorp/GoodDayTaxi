package com.gooddaytaxi.dispatch.presentation.external.mapper.command;

import com.gooddaytaxi.dispatch.application.usecase.reject.DispatchRejectCommand;

import java.util.UUID;

public class DispatchRejectCommandMapper {
    public static DispatchRejectCommand toCommand (UUID userId, UUID dispatchId){
        return DispatchRejectCommand.builder()
                .dispatchId(dispatchId)
                .driverId(userId)
                .build();
    }
}
