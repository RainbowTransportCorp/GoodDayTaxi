package com.gooddaytaxi.payment.application.command;

import java.util.UUID;

public record PaymentAmountChangeCommand(UUID paymentId,
                                         Long amount) {
}
