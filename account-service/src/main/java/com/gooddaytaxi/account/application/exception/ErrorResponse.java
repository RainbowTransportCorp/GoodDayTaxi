package com.gooddaytaxi.account.application.exception;

import com.gooddaytaxi.common.core.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * API 에러 응답 DTO
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {

    private final String code;
    private final String message;

    /**
     * ErrorCode로부터 ErrorResponse 생성
     *
     * @param errorCode 공통 에러 코드
     * @return ErrorResponse 인스턴스
     */
    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage()
        );
    }
}
