package com.gooddaytaxi.payment.application.command;

import java.util.UUID;

public record PaymentCreateCommand(Long amount,
                                   String method,
                                   Long passengerId,
                                   Long driverId,
                                   UUID tripId) {
}
