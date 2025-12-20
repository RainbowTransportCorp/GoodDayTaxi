package com.gooddaytaxi.payment.presentation.external.dto.response.reqeustRufund;

import java.util.UUID;

public record RefundRequestReadResponseDto(UUID requestId,
                                           UUID paymentId,
                                           String reason,
                                           String response,
                                           String status) {
}
