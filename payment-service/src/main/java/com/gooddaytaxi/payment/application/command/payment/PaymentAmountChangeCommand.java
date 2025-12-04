package com.gooddaytaxi.payment.application.command.payment;

import java.util.UUID;

public record PaymentAmountChangeCommand(UUID paymentId,
                                         Long amount) {
}
