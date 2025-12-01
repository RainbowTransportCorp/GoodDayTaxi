package com.gooddaytaxi.account.application.exception;

import com.gooddaytaxi.common.core.exception.BusinessException;
import com.gooddaytaxi.common.core.exception.ErrorLevel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 핸들러
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * BusinessException 처리
     *
     * @param e 비즈니스 예외
     * @return ErrorResponse와 적절한 HTTP 상태 코드
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handle(BusinessException e) {

        HttpStatus status = mapErrorLevelToHttpStatus(e.getErrorCode().getLevel());

        return ResponseEntity
                .status(status)
                .body(ErrorResponse.from(e.getErrorCode()));
    }

    /**
     * ErrorLevel을 HttpStatus로 매핑
     *
     * @param level 에러 레벨
     * @return 해당하는 HttpStatus
     */
    private HttpStatus mapErrorLevelToHttpStatus(ErrorLevel level) {
        return switch (level) {
            case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case METHOD_NOT_ALLOWED -> HttpStatus.METHOD_NOT_ALLOWED;
            case CONFLICT -> HttpStatus.CONFLICT;
            case BAD_GATEWAY -> HttpStatus.BAD_GATEWAY;
            case INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            case SERVICE_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
            case TIMEOUT -> HttpStatus.GATEWAY_TIMEOUT;
        };
    }
}
