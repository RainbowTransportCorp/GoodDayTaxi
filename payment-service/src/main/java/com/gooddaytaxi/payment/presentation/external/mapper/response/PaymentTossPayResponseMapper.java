package com.gooddaytaxi.payment.presentation.external.mapper.response;

import com.gooddaytaxi.payment.application.result.PaymentTossPayResult;
import com.gooddaytaxi.payment.presentation.external.dto.response.PaymentTossPayResponseDto;

public class PaymentTossPayResponseMapper {
    public static PaymentTossPayResponseDto toResponse(PaymentTossPayResult result) {
        return new PaymentTossPayResponseDto(
                result.paymentId(),
                result.amount(),
                result.status(),
                result.method());
    }
}
