package com.gooddaytaxi.payment.presentation.external.mapper.command.payment;

import com.gooddaytaxi.payment.application.command.payment.PaymentCreateCommand;
import com.gooddaytaxi.payment.presentation.external.dto.request.payment.PaymentCreateRequestDto;

public class PaymentCreateMapper {
    public static PaymentCreateCommand toCommand(PaymentCreateRequestDto requestDto) {
        return new PaymentCreateCommand(
                requestDto.amount(),
                requestDto.method(),
                requestDto.passengerId(),
                requestDto.driverId(),
                requestDto.tripId());
    }
}
