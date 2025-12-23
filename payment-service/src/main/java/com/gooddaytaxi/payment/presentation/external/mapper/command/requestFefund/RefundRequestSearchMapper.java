package com.gooddaytaxi.payment.presentation.external.mapper.command.requestFefund;

import com.gooddaytaxi.payment.application.command.refundRequest.RefundReqeustSearchCommand;
import com.gooddaytaxi.payment.presentation.external.mapper.command.common.PeriodMapper;

import java.util.UUID;

public class RefundRequestSearchMapper {
    public static RefundReqeustSearchCommand toCommand(Integer page, Integer size, String status, String reasonKeyword, String searchPeriod, String startDay, String endDay, String sortBy, Boolean sortAscending) {
        return new RefundReqeustSearchCommand(
                page ==  null ? 1 : page,
                size==  null ? 10 : size,
                null,
                status,
                reasonKeyword,
                null,
                null,
                null,
                PeriodMapper.map(searchPeriod, startDay, endDay, true),
                PeriodMapper.map(searchPeriod, startDay, endDay, false),
                sortBy== null ? "createdAt" : sortBy,   // 기본값 넣기
                 sortAscending == null || sortAscending
        );
    }

    public static RefundReqeustSearchCommand toAdminCommand(Integer page, Integer size, UUID paymentId, String status, String reasonKeyword, String method, UUID passengerId, UUID driverId, String searchPeriod, String startDay, String endDay, String sortBy, Boolean sortAscending) {
        return new RefundReqeustSearchCommand(
                page ==  null ? 1 : page,
                size==  null ? 10 : size,
                paymentId,
                status,
                reasonKeyword,
                method,
                passengerId,
                driverId,
                PeriodMapper.map(searchPeriod, startDay, endDay, true),
                PeriodMapper.map(searchPeriod, startDay, endDay, false),
                sortBy== null ? "createdAt" : sortBy,   // 기본값 넣기
                sortAscending == null || sortAscending
        );

    }
}
