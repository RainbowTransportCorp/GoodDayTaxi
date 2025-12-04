package com.gooddaytaxi.payment.application.command.payment;

import java.util.UUID;

public record PaymentMethodChangeCommand(UUID paymentId,
                                         String method) {
}
