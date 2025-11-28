package com.gooddaytaxi.payment.presentation.external.mapper.command;

import com.gooddaytaxi.payment.application.command.PaymentCreateCommand;
import com.gooddaytaxi.payment.presentation.external.dto.request.PaymentCreateRequestDto;

public class PaymentCreateMapper {
    public static PaymentCreateCommand toCommand(PaymentCreateRequestDto requestDto) {
        return PaymentCreateCommand
                .builder()
                .amount(requestDto.getAmount())
                .method(requestDto.getMethod())
                .passengerId(requestDto.getPassengerId())
                .driverId(requestDto.getDriverId())
                .tripId(requestDto.getTripId())
                .build();
    }
}
