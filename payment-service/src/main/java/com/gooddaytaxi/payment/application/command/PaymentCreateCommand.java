package com.gooddaytaxi.payment.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateCommand {
    private Long amount;
    private String method;
    private Long passengerId;
    private Long driverId;
    private UUID tripId;
}
