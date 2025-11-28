package com.gooddaytaxi.dispatch.application.commend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchCreateCommand {
    private Long passengerId;
    private String pickupAddress;
    private String destinationAddress;
}
