package com.gooddaytaxi.payment.presentation.external.dto.response.reqeustRufund;

import com.gooddaytaxi.payment.domain.enums.RefundRequestStatus;

import java.util.UUID;

public record RefundRequestReadResponseDto(UUID requestId,
                                           UUID paymentId,
                                           String reason,
                                           String response,
                                           RefundRequestStatus status) {
}
