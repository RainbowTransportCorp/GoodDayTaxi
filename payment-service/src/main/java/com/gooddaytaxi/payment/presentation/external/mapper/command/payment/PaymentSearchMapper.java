package com.gooddaytaxi.payment.presentation.external.mapper.command.payment;

import com.gooddaytaxi.payment.application.command.payment.PaymentSearchCommand;
import com.gooddaytaxi.payment.presentation.external.dto.request.payment.PaymentSearchRequestDto;
import com.gooddaytaxi.payment.presentation.external.mapper.command.common.PeriodMapper;


public class PaymentSearchMapper {
    public static PaymentSearchCommand toCommand(PaymentSearchRequestDto dto) {
        return new PaymentSearchCommand(
                dto.page() ==  null ? 1 : dto.page(),
                dto.size()==  null ? 10 : dto.size(),
                dto.method(),
                dto.status(),
                dto.passengerId(),
                dto.driverId(),
                dto.tripId(),
                dto.searchPeriod(),
                PeriodMapper.map(dto.searchPeriod(), dto.startDay(), dto.endDay(), true),
                PeriodMapper.map(dto.searchPeriod(), dto.startDay(), dto.endDay(), false),
                dto.sortBy()== null ? "createdAt" : dto.sortBy(),   // 기본값 넣기
                 dto.sortAscending() == null || dto.sortAscending()
        );
    }

}
