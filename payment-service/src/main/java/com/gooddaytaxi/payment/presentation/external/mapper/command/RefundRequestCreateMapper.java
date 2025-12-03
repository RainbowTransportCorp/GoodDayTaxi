package com.gooddaytaxi.payment.presentation.external.mapper.command;

import com.gooddaytaxi.payment.application.command.RefundRequestCreateCommand;
import com.gooddaytaxi.payment.presentation.external.dto.request.RefundRequestCreateRequestDto;
import jakarta.validation.Valid;

public class RefundRequestCreateMapper {
    //승객의 환불 요청 생성 매퍼
    public static RefundRequestCreateCommand toRequestCommand(@Valid RefundRequestCreateRequestDto requestDto) {
        return new RefundRequestCreateCommand(
                requestDto.paymentId(),
                requestDto.reason()
        );
    }
}
