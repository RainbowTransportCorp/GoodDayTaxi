package com.gooddaytaxi.dispatch.presentation.external.mapper.command;

import com.gooddaytaxi.dispatch.application.validator.UserRole;
import com.gooddaytaxi.dispatch.application.commend.DispatchCreateCommand;
import com.gooddaytaxi.dispatch.presentation.external.dto.request.DispatchCreateRequestDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DispatchCreateCommandMapper {

    public static DispatchCreateCommand toCommand(UUID userId, String role , DispatchCreateRequestDto dto) {
        return DispatchCreateCommand.builder()
                .passengerId(userId)
                .role(UserRole.valueOf(role))
                .pickupAddress(dto.getPickupAddress())
                .destinationAddress(dto.getDestinationAddress())
                .build();
    }
}
