package com.gooddaytaxi.payment.presentation.external.mapper.response.payment;

import com.gooddaytaxi.payment.application.result.payment.PaymentApproveResult;
import com.gooddaytaxi.payment.presentation.external.dto.response.payment.PaymentApproveResponseDto;

public class PaymentApproveResponseMapper {
    public static PaymentApproveResponseDto toResponse(PaymentApproveResult result) {
        return new PaymentApproveResponseDto(
                result.paymentId(),
                result.amount(),
                result.status(),
                result.method());
    }
}
