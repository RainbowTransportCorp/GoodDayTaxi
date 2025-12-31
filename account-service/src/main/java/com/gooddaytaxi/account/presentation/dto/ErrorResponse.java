package com.gooddaytaxi.account.presentation.dto;

import com.gooddaytaxi.account.domain.exception.AccountErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Account 서비스 에러 응답 클래스
 * dispatch 서비스와 동일한 형식으로 통일
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {
    
    private final String code;
    private final String message;
    
    /**
     * AccountErrorCode로부터 ErrorResponse 생성
     */
    public static ErrorResponse from(AccountErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage()
        );
    }
}