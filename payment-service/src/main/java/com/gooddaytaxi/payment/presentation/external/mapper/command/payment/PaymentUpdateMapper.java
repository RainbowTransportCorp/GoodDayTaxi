package com.gooddaytaxi.payment.presentation.external.mapper.command.payment;

import com.gooddaytaxi.payment.application.command.payment.PaymentAmountChangeCommand;
import com.gooddaytaxi.payment.presentation.external.dto.request.payment.PaymentAmountChangeRequestDto;
import jakarta.validation.Valid;

public class PaymentUpdateMapper {
    public static PaymentAmountChangeCommand toAmountCommand(@Valid PaymentAmountChangeRequestDto requestDto) {
        return new PaymentAmountChangeCommand(
                requestDto.paymentId(),
                requestDto.amount()
        );
    }
}
