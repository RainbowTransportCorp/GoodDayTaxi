package com.gooddaytaxi.payment.presentation.external.dto.request.requestRefund;

import java.util.UUID;

public record RefundRequestCreateRequestDto(UUID paymentId,
                                            String reason) {
}
