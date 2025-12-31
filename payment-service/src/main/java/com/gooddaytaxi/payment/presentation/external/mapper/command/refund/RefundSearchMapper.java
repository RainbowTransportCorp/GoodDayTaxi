package com.gooddaytaxi.payment.presentation.external.mapper.command.refund;

import com.gooddaytaxi.payment.application.command.refund.RefundSearchCommand;
import com.gooddaytaxi.payment.presentation.external.mapper.command.common.PeriodMapper;

import java.util.UUID;

public class RefundSearchMapper {
    public static RefundSearchCommand toCommand(Integer page, Integer size, String status, String reason, Boolean existRequest, UUID tripId, String method, Long minAmount, Long maxAmount, String searchPeriod, String startDay, String endDay, String sortBy, Boolean sortAscending) {
        return new RefundSearchCommand(
                page==null || page<=0 ? 1 : page,
                size==null || size<=0 ? 10 : size,
                status,
                reason,
                existRequest,
                null,
                null,
                tripId,
                method,
                minAmount,
                maxAmount,
                searchPeriod,
                PeriodMapper.map(searchPeriod, startDay, endDay, true),
                PeriodMapper.map(searchPeriod, startDay, endDay, false),
                sortBy== null ? "canceledAt" : sortBy,   // 기본값 넣기
                sortAscending == null || sortAscending);

    }
    public static RefundSearchCommand toAdminCommand(Integer page, Integer size, String status, String reason, Boolean existRequest, UUID passengerId, UUID driverId, UUID tripId, String method, Long minAmount, Long maxAmount, String searchPeriod, String startDay, String endDay, String sortBy, Boolean sortAscending) {
        return new RefundSearchCommand(
                page==null || page<=0 ? 1 : page,
                size==null || size<=0 ? 10 : size,
                status,
                reason,
                existRequest,
                passengerId,
                driverId,
                tripId,
                method,
                minAmount,
                maxAmount,
                searchPeriod,
                PeriodMapper.map(searchPeriod, startDay, endDay, true),
                PeriodMapper.map(searchPeriod, startDay, endDay, false),
                sortBy== null ? "canceledAt" : sortBy,   // 기본값 넣기
                sortAscending == null || sortAscending);

    }
}
