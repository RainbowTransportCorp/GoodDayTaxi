package com.gooddaytaxi.dispatch.presentation.external.mapper.command;

import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;
import com.gooddaytaxi.dispatch.application.usecase.reject.DispatchRejectCommand;

import java.util.UUID;

public class DispatchRejectCommandMapper {
    public static DispatchRejectCommand toCommand (UUID userId, String role, UUID dispatchId){
        return DispatchRejectCommand.builder()
                .dispatchId(dispatchId)
                .role(UserRole.valueOf(role))
                .driverId(userId)
                .build();
    }
}
