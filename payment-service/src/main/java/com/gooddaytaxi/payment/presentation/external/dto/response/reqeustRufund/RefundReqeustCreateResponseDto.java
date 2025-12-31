package com.gooddaytaxi.payment.presentation.external.dto.response.reqeustRufund;

import java.util.UUID;

public record RefundReqeustCreateResponseDto(UUID refundRequestId, String message) {
}
