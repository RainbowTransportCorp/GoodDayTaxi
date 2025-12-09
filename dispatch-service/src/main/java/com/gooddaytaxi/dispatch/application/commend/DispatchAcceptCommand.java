package com.gooddaytaxi.dispatch.application.commend;

import com.gooddaytaxi.dispatch.application.validator.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DispatchAcceptCommand {
    private UUID dispatchId;
    private UUID driverId;
    private UserRole role;
}
