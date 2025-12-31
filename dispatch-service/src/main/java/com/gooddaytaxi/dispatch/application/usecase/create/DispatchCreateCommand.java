package com.gooddaytaxi.dispatch.application.usecase.create;

import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DispatchCreateCommand {
    private UUID passengerId;
    private UserRole role;
    private String pickupAddress;
    private String destinationAddress;
}
