package com.gooddaytaxi.payment.presentation.external.mapper.command.requestFefund;

import com.gooddaytaxi.payment.application.command.refundRequest.RefundRequestCreateCommand;
import com.gooddaytaxi.payment.application.command.refundRequest.RefundRequestResponseCreateCommand;
import com.gooddaytaxi.payment.presentation.external.dto.request.requestRefund.RefundRequestCreateRequestDto;
import com.gooddaytaxi.payment.presentation.external.dto.request.requestRefund.RefundRequestResponseRequestDto;

public class RefundRequestCreateMapper {
    //승객의 환불 요청 생성 매퍼
    public static RefundRequestCreateCommand toRequestCommand(RefundRequestCreateRequestDto requestDto) {
        return new RefundRequestCreateCommand(
                requestDto.paymentId(),
                requestDto.reason()
        );
    }

    public static RefundRequestResponseCreateCommand toResponseCommand(RefundRequestResponseRequestDto requestDto) {
        return new RefundRequestResponseCreateCommand(
                requestDto.requestId(),
                requestDto.approve(),
                requestDto.response()
        );
    }
}
