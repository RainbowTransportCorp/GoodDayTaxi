package com.gooddaytaxi.dispatch.presentation.external.mapper.command;

import com.gooddaytaxi.dispatch.application.commend.DispatchAcceptCommand;
import com.gooddaytaxi.dispatch.application.validator.UserRole;

import java.util.UUID;

public class DispatchAcceptCommandMapper {
    public static DispatchAcceptCommand toCommand (UUID userId, String role, UUID dispatchId){
        return DispatchAcceptCommand.builder()
                .dispatchId(dispatchId)
                .driverId(userId)
                .role(UserRole.valueOf(role))
                .build();
    }
}
