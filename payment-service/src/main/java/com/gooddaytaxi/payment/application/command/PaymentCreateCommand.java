package com.gooddaytaxi.payment.application.command;

import java.util.UUID;

public record PaymentCreateCommand(Long amount,
                                   String method,
                                   UUID passengerId,
                                   UUID driverId,
                                   UUID tripId) {
}
