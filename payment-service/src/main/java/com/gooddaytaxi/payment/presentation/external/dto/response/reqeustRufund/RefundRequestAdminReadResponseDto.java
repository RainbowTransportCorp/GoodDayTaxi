package com.gooddaytaxi.payment.presentation.external.dto.response.reqeustRufund;

import com.gooddaytaxi.payment.presentation.external.dto.response.payment.PaymentAdminReadResponseDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.refund.RefundAdminReadResponseDto;

import java.util.UUID;

public record RefundRequestAdminReadResponseDto(UUID requestId,
                                                String reason,
                                                String response,
                                                String status,
                                                PaymentAdminReadResponseDto payment,
                                                RefundAdminReadResponseDto refund) {
}
