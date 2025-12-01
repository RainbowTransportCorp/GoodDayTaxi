package com.gooddaytaxi.payment.presentation.external.mapper.command;

import com.gooddaytaxi.payment.presentation.external.dto.request.PaymentTossPayRequestDto;
import com.gooddaytaxi.payment.presentation.external.mapper.response.PaymentTossPayCommand;
import jakarta.validation.Valid;

public class PaymentTossPayMapper {
    public static PaymentTossPayCommand toCommand(@Valid PaymentTossPayRequestDto requestDto) {
        return new PaymentTossPayCommand(
                requestDto.orderId(),
                requestDto.paymentKey(),
                requestDto.amount());
    }
}
