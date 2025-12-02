package com.gooddaytaxi.payment.presentation.external.mapper.response;

import com.gooddaytaxi.payment.application.result.PaymentApproveResult;
import com.gooddaytaxi.payment.presentation.external.dto.response.PaymentApproveResponseDto;

public class PaymentApproveResponseMapper {
    public static PaymentApproveResponseDto toResponse(PaymentApproveResult result) {
        return new PaymentApproveResponseDto(
                result.paymentId(),
                result.amount(),
                result.status(),
                result.method());
    }
}
