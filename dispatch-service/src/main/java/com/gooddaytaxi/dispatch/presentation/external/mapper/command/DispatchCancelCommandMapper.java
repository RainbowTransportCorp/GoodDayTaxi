package com.gooddaytaxi.dispatch.presentation.external.mapper.command;

import com.gooddaytaxi.dispatch.application.usecase.cancel.DispatchCancelCommand;
import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;

import java.util.UUID;

public class DispatchCancelCommandMapper {
    public static DispatchCancelCommand toCommand (UUID passengerId, String role, UUID dispatchId){
        return DispatchCancelCommand.builder()
                .passengerId(passengerId)
                .dispatchId(dispatchId)
                .role(UserRole.valueOf(role))
                .build();
    }
}
