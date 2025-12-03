package com.gooddaytaxi.payment.application.command;

import java.util.UUID;

public record RefundRequestCreateCommand(UUID paymentId,
                                         String reason) {
}
