package com.gooddaytaxi.payment.presentation.external.mapper.command.refund;

import com.gooddaytaxi.payment.application.command.refund.RefundSearchCommand;
import com.gooddaytaxi.payment.presentation.external.dto.request.refund.RefundAdminSearchRequestDto;
import com.gooddaytaxi.payment.presentation.external.dto.request.refund.RefundSearchRequestDto;
import com.gooddaytaxi.payment.presentation.external.mapper.command.common.PeriodMapper;

public class RefundSearchMapper {
    public static RefundSearchCommand toCommand(RefundSearchRequestDto dto) {
        return new RefundSearchCommand(
                dto.page() ==  null ? 1 : dto.page(),
                dto.size()==  null ? 10 : dto.size(),
                dto.status(),
                dto.reason(),
                dto.existRequest(),
                null,
                null,
                dto.tripId(),
                dto.method(),
                dto.minAmount(),
                dto.maxAmount(),
                dto.searchPeriod(),
                PeriodMapper.map(dto.searchPeriod(), dto.startDay(), dto.endDay(), true),
                PeriodMapper.map(dto.searchPeriod(), dto.startDay(), dto.endDay(), false),
                dto.sortBy()== null ? "canceledAt" : dto.sortBy(),   // 기본값 넣기
                dto.sortAscending() == null || dto.sortAscending());

    }
    public static RefundSearchCommand toAdminCommand(RefundAdminSearchRequestDto dto) {
        return new RefundSearchCommand(
                dto.page() ==  null ? 1 : dto.page(),
                dto.size()==  null ? 10 : dto.size(),
                dto.status(),
                dto.reason(),
                dto.existRequest(),
                dto.passengerId(),
                dto.driverId(),
                dto.tripId(),
                dto.method(),
                dto.minAmount(),
                dto.maxAmount(),
                dto.searchPeriod(),
                PeriodMapper.map(dto.searchPeriod(), dto.startDay(), dto.endDay(), true),
                PeriodMapper.map(dto.searchPeriod(), dto.startDay(), dto.endDay(), false),
                dto.sortBy()== null ? "canceledAt" : dto.sortBy(),   // 기본값 넣기
                dto.sortAscending() == null || dto.sortAscending());

    }
}
