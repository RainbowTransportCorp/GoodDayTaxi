package com.gooddaytaxi.payment.presentation.external.mapper.response;

import com.gooddaytaxi.payment.application.result.PaymentCreateResult;
import com.gooddaytaxi.payment.presentation.external.dto.response.PaymentCreateResponseDto;

public class PaymentResponseMapper {
    public static PaymentCreateResponseDto toCreateResponse(PaymentCreateResult result) {
        return PaymentCreateResponseDto.builder()
                .paymentId(result.getPaymentId())
                .method(result.getMethod())
                .amount(result.getAmount())
                .passengerId(result.getPassengerId())
                .driverId(result.getDriverId())
                .tripId(result.getTripId())
                .build();
    }
}
