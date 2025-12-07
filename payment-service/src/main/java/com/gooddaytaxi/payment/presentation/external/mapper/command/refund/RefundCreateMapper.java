package com.gooddaytaxi.payment.presentation.external.mapper.command.refund;

import com.gooddaytaxi.payment.application.command.refund.RefundCreateCommand;
import com.gooddaytaxi.payment.presentation.external.dto.request.refund.RefundCreateRequestDto;

public class RefundCreateMapper {
    public static RefundCreateCommand toCommand(RefundCreateRequestDto requestDto) {
        return new RefundCreateCommand(
                requestDto.reason(),
                requestDto.detailReason(),
                requestDto.requestId()
        );
    }
}
