package com.gooddaytaxi.payment.presentation.external.mapper.command.payment;

import com.gooddaytaxi.payment.application.command.payment.PaymentAmountChangeCommand;
import com.gooddaytaxi.payment.application.command.payment.PaymentMethodChangeCommand;
import com.gooddaytaxi.payment.presentation.external.dto.request.payment.PaymentAmountChangeRequestDto;
import com.gooddaytaxi.payment.presentation.external.dto.request.payment.PaymentMethodChangeRequestDto;

public class PaymentUpdateMapper {
    public static PaymentAmountChangeCommand toAmountCommand(PaymentAmountChangeRequestDto requestDto) {
        return new PaymentAmountChangeCommand(
                requestDto.paymentId(),
                requestDto.amount()
        );
    }

    public static PaymentMethodChangeCommand toMethodCommand(PaymentMethodChangeRequestDto requestDto) {
        return new PaymentMethodChangeCommand(
                requestDto.paymentId(),
                requestDto.method()
        );
    }
}
