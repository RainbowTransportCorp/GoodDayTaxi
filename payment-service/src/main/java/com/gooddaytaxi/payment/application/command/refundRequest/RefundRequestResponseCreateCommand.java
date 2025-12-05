package com.gooddaytaxi.payment.application.command.refundRequest;

import java.util.UUID;

public record RefundRequestResponseCreateCommand(UUID requestId, Boolean approve, String response) {
}
