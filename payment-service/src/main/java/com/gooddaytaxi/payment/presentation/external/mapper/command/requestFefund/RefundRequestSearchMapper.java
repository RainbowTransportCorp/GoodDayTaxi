package com.gooddaytaxi.payment.presentation.external.mapper.command.requestFefund;

import com.gooddaytaxi.payment.application.command.refundRequest.RefundReqeustSearchCommand;
import com.gooddaytaxi.payment.presentation.external.dto.request.requestRefund.RefundRequestSearchRequestDto;
import com.gooddaytaxi.payment.presentation.external.mapper.command.common.PeriodMapper;

public class RefundRequestSearchMapper {
    public static RefundReqeustSearchCommand toCommand(RefundRequestSearchRequestDto dto) {
        return new RefundReqeustSearchCommand(
                dto.page() ==  null ? 1 : dto.page(),
                dto.size()==  null ? 10 : dto.size(),
                dto.paymentId(),
                dto.status(),
                dto.reasonKeyword(),
                dto.method(),
                dto.passengerId(),
                dto.driverId(),
                dto.searchPeriod(),
                PeriodMapper.map(dto.searchPeriod(), dto.startDay(), dto.endDay(), true),
                PeriodMapper.map(dto.searchPeriod(), dto.startDay(), dto.endDay(), false),
                dto.sortBy()== null ? "createdAt" : dto.sortBy(),   // 기본값 넣기
                 dto.sortAscending() == null || dto.sortAscending()
        );

    }
}
