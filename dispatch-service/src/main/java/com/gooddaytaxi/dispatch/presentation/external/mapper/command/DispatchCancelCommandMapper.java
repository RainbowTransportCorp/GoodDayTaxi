package com.gooddaytaxi.dispatch.presentation.external.mapper.command;

import com.gooddaytaxi.dispatch.application.commend.DispatchCancelCommand;

import java.util.UUID;

public class DispatchCancelCommandMapper {
    public static DispatchCancelCommand toCommand (UUID userId){
        return DispatchCancelCommand.builder()
                .dispatchId(userId)
                .build();
    }
}
