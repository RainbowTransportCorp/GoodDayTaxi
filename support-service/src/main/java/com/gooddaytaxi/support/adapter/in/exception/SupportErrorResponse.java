package com.gooddaytaxi.support.adapter.in.exception;

import com.gooddaytaxi.support.domain.exception.SupportErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SupportErrorResponse {

    private final String code;
    private final String message;

    public static SupportErrorResponse from(SupportErrorCode errorCode) {
        return new SupportErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage()
        );
    }
}
