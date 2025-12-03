package com.gooddaytaxi.payment.domain.vo;

import com.gooddaytaxi.common.core.exception.BusinessException;
import com.gooddaytaxi.common.core.exception.ErrorCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

public enum SearchPeriod {
    ALL,
    TODAY,
    THIS_WEEK,
    THIS_MONTH,
    THIS_YEAR,
    ENTER_DIRECTLY;   //직접 입력

    public static SearchPeriod of(String value) {
        for (SearchPeriod period : SearchPeriod.values()) {
            if (period.name().equalsIgnoreCase(value)) {
                return period;
            }
        }
        throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
    }

    public static LocalDateTime selectedPreset(String searchPeriod) {
        return switch (searchPeriod) {
            case "TODAY" -> LocalDateTime.now().toLocalDate().atStartOfDay();
            case "THIS_WEEK" -> LocalDate.now().minusDays(7).atStartOfDay();
            case "THIS_MONTH" -> LocalDate.now().minusMonths(1).atStartOfDay();
            default ->   // THIS_YEAR
                    LocalDate.now().minusYears(1).atStartOfDay();
        };
    }
}
