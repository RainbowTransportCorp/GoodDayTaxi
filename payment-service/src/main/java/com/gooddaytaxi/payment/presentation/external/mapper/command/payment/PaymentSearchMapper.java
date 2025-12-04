package com.gooddaytaxi.payment.presentation.external.mapper.command.payment;

import com.gooddaytaxi.payment.application.command.PaymentSearchCommand;
import com.gooddaytaxi.payment.application.exception.PaymentErrorCode;
import com.gooddaytaxi.payment.application.exception.PaymentException;
import com.gooddaytaxi.payment.application.command.payment.PaymentSearchCommand;
import com.gooddaytaxi.payment.domain.vo.SearchPeriod;
import com.gooddaytaxi.payment.presentation.external.dto.request.payment.PaymentSearchRequestDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


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
                periodMapper(dto.searchPeriod(), dto.startDay(), dto.endDay(), true),
                periodMapper(dto.searchPeriod(), dto.startDay(), dto.endDay(), false),
                dto.sortBy()== null ? "createdAt" : dto.sortBy(),   // 기본값 넣기
                 dto.sortAscending() == null || dto.sortAscending()
        );
    }
    private static LocalDateTime periodMapper(String searchPeriod,String startDay, String endDay,  Boolean isStart) {
        if(searchPeriod.equals("ALL")) return null;
        else if (searchPeriod.equals("ENTER_DIRECTLY")) {
            if(startDay == null || endDay == null) throw new PaymentException(PaymentErrorCode.PERIOD_REQUIRED_FOR_SEARCH);
            if(isStart) return LocalDateTime.of(LocalDate.parse(startDay), LocalTime.of(0,0,0));
            else return LocalDateTime.of(LocalDate.parse(endDay), LocalTime.of(23,59,59));
        }
        else {
            if(isStart) return SearchPeriod.selectedPreset(searchPeriod);
            return null;
        }
    }
}
