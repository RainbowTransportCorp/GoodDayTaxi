package com.gooddaytaxi.payment.presentation.external.mapper.command.refund;

import com.gooddaytaxi.payment.application.command.refund.RefundCreateCommand;
import com.gooddaytaxi.payment.presentation.external.dto.request.refund.RefundCreateRequestDto;

public class RefundCreateMapper {
    public static RefundCreateCommand toTosspayCommand(RefundCreateRequestDto requestDto) {
        return new RefundCreateCommand(
                requestDto.reason(),
                requestDto.incidentAt(),
                requestDto.incidentSummary(),
                null,
                requestDto.requestId()
        );
    }
    public static RefundCreateCommand toPhysicalCommand(RefundCreateRequestDto requestDto) {
        return new RefundCreateCommand(
                requestDto.reason(),
                requestDto.incidentAt(),
                requestDto.incidentSummary(),
                requestDto.executedAt(),
                requestDto.requestId()
        );
    }
}
