package com.gooddaytaxi.payment.presentation.external.mapper.command.payment;

import com.gooddaytaxi.payment.application.command.payment.PaymentSearchCommand;
import com.gooddaytaxi.payment.presentation.external.mapper.command.common.PeriodMapper;

import java.util.UUID;


public class PaymentSearchMapper {
    public static PaymentSearchCommand toCommand(Integer page, Integer size, String method, String status, String searchPeriod, String startDay, String endDay, String sortBy, Boolean sortAscending) {
        return new PaymentSearchCommand(
                page==null || page<0 ? 1 : page,
                size==null || size<0 ? 10 : size,
                method,
                status,
                null,
                null,
                null,
                searchPeriod,
                PeriodMapper.map(searchPeriod, startDay, endDay, true),
                PeriodMapper.map(searchPeriod, startDay, endDay, false),
                sortBy== null ? "createdAt" : sortBy,   // 기본값 넣기
                sortAscending == null || sortAscending
        );
    }

    public static PaymentSearchCommand toAdminCommand(Integer page, Integer size, String method, String status, UUID passengerId, UUID driverId, UUID tripId, String searchPeriod, String startDay, String endDay, String sortBy, Boolean sortAscending) {
        return new PaymentSearchCommand(
                page==null || page<0 ? 1 : page,
                size==null || size<0 ? 10 : size,
                method,
                status,
                passengerId,
                driverId,
                tripId,
                searchPeriod,
                PeriodMapper.map(searchPeriod, startDay, endDay, true),
                PeriodMapper.map(searchPeriod, startDay, endDay, false),
                sortBy== null ? "createdAt" : sortBy,   // 기본값 넣기
                sortAscending == null || sortAscending
        );
    }
}
