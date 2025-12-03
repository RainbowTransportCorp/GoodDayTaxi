package com.gooddaytaxi.payment.presentation.external.dto.request;

import java.util.UUID;

public record RefundReqeustCreateResponseDto(UUID refundRequestId, String message) {
}
