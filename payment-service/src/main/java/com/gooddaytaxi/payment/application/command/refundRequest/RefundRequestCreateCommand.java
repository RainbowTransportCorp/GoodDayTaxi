package com.gooddaytaxi.payment.application.command.refundRequest;

import java.util.UUID;

public record RefundRequestCreateCommand(UUID paymentId,
                                         String reason) {
}
