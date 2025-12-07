package com.gooddaytaxi.payment.application.command.refund;

import java.util.UUID;

public record RefundCreateCommand(String reason,
                                  String detailReason,
                                  UUID requestId) {
}
