package com.gooddaytaxi.payment.presentation.external.mapper.command.payment;

import com.gooddaytaxi.payment.application.command.payment.PaymentCancelCommand;
import com.gooddaytaxi.payment.presentation.external.dto.request.payment.PaymentCancelRequestDto;

public class PaymentCancelMapper {

    public static PaymentCancelCommand toCommand(PaymentCancelRequestDto requestDto) {
        return new PaymentCancelCommand(
                requestDto.paymentId(),
                requestDto.cancelReason()
        );
    }
}
