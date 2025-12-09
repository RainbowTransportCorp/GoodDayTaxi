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
public class DispatchCancelCommand {
    private UUID dispatchId;
    private UUID passengerId;
    private UserRole role;
}
