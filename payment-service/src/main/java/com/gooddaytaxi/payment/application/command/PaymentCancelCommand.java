package com.gooddaytaxi.payment.application.command;

import java.util.UUID;

public record PaymentCancelCommand(UUID paymentId,
                                   String cancelReason) {
}
