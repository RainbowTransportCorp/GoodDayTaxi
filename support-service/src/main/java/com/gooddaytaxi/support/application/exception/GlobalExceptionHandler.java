package com.gooddaytaxi.support.application.exception;

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
            case CONFLICT -> HttpStatus.CONFLICT;
            case METHOD_NOT_ALLOWED -> HttpStatus.METHOD_NOT_ALLOWED;
            case INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            case SERVICE_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
            case TIMEOUT -> HttpStatus.GATEWAY_TIMEOUT;
        };
    }
}
