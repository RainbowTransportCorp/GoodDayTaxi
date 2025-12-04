package com.gooddaytaxi.payment.application.command.payment;

import java.util.UUID;

public record PaymentCancelCommand(UUID paymentId,
                                   String cancelReason) {
}
