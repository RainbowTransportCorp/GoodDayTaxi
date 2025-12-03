package com.gooddaytaxi.payment.presentation.external.mapper.command;

import com.gooddaytaxi.payment.application.command.PaymentAmountChangeCommand;
import com.gooddaytaxi.payment.presentation.external.dto.request.PaymentAmountChangeRequestDto;
import jakarta.validation.Valid;

public class PaymentUpdateMapper {
    public static PaymentAmountChangeCommand toAmountCommand(@Valid PaymentAmountChangeRequestDto requestDto) {
        return new PaymentAmountChangeCommand(
                requestDto.paymentId(),
                requestDto.amount()
        );
    }
}
