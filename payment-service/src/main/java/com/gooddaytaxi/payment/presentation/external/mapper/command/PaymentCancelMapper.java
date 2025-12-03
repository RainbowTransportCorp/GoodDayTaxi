package com.gooddaytaxi.payment.presentation.external.mapper.command;

import com.gooddaytaxi.payment.application.command.PaymentCancelCommand;
import com.gooddaytaxi.payment.presentation.external.dto.request.PaymentCancelRequestDto;

public class PaymentCancelMapper {

    public static PaymentCancelCommand toCommand(PaymentCancelRequestDto requestDto) {
        return new PaymentCancelCommand(
                requestDto.paymentId(),
                requestDto.cancelReason()
        );
    }
}
