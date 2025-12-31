package com.gooddaytaxi.payment.presentation.external.mapper.command.common;

import com.gooddaytaxi.payment.application.exception.PaymentErrorCode;
import com.gooddaytaxi.payment.application.exception.PaymentException;
import com.gooddaytaxi.payment.domain.vo.SearchPeriod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class PeriodMapper {
    public static LocalDateTime map(String searchPeriod, String startDay, String endDay, Boolean isStart) {
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
