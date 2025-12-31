package com.gooddaytaxi.dispatch.application.exception;

import com.gooddaytaxi.common.core.exception.BusinessException;
import com.gooddaytaxi.common.core.exception.ErrorLevel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handle(BusinessException e) {

        HttpStatus status = mapErrorLevelToHttpStatus(e.getErrorCode().getLevel());

        return ResponseEntity
                .status(status)
                .body(ErrorResponse.from(e.getErrorCode())); // ← of 대신 from
    }

    private HttpStatus mapErrorLevelToHttpStatus(ErrorLevel level) {
        return switch (level) {
            case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case METHOD_NOT_ALLOWED -> HttpStatus.METHOD_NOT_ALLOWED;
            case CONFLICT -> HttpStatus.CONFLICT;

            // 새로 추가된 BAD_GATEWAY 매핑
            case BAD_GATEWAY -> HttpStatus.BAD_GATEWAY;

            case INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            case SERVICE_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;

            // TIMEOUT은 상황에 따라 408 또는 504 선택 가능 → 지금은 504로 통일
            case TIMEOUT -> HttpStatus.GATEWAY_TIMEOUT;
        };
    }
}