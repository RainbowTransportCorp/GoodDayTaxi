package com.gooddaytaxi.dispatch.presentation.external.mapper.command;

import com.gooddaytaxi.dispatch.application.commend.DispatchAcceptCommand;

import java.util.UUID;

public class DispatchAcceptCommandMapper {
    public static DispatchAcceptCommand toCommand (UUID userId, UUID dispatchId){
        return DispatchAcceptCommand.builder()
                .dispatchId(dispatchId)
                .driverId(userId)
                .build();
    }
}
