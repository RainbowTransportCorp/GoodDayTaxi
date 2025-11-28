package com.gooddaytaxi.infra.gateway.presentation;

import com.gooddaytaxi.infra.gateway.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class TokenExceptionHandler {

    @ExceptionHandler(EmptyClaimsException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleEmptyClaimsException(EmptyClaimsException e) {
        log.warn("토큰 클레임 오류 - 메시지: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                e.getErrorCode().getCode(),
                e.getErrorCode().getMessage(),
                e.getErrorCode().getStatus()
        );

        return ResponseEntity.status(errorResponse.status()).body(ApiResponse.error(errorResponse));
    }

    @ExceptionHandler(ExpiredException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleExpiredException(ExpiredException e) {
        log.warn("토큰 만료 - 메시지: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                e.getErrorCode().getCode(),
                e.getErrorCode().getMessage(),
                e.getErrorCode().getStatus()
        );

        return ResponseEntity.status(errorResponse.status()).body(ApiResponse.error(errorResponse));
    }

    @ExceptionHandler(InvalidSignatureException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleInvalidSignatureException(InvalidSignatureException e) {
        log.warn("토큰 서명 오류 - 메시지: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                e.getErrorCode().getCode(),
                e.getErrorCode().getMessage(),
                e.getErrorCode().getStatus()
        );
        return ResponseEntity.status(errorResponse.status()).body(ApiResponse.error(errorResponse));
    }

    @ExceptionHandler(MalFormedException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMalFormedException(MalFormedException e) {
        log.warn("토큰 형식 오류 - 메시지: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                e.getErrorCode().getCode(),
                e.getErrorCode().getMessage(),
                e.getErrorCode().getStatus()
        );
        return ResponseEntity.status(errorResponse.status()).body(ApiResponse.error(errorResponse));
    }

    @ExceptionHandler(UnsupportedException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleUnsupportedException(UnsupportedException e) {
        log.warn("지원하지 않는 토큰 - 메시지: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                e.getErrorCode().getCode(),
                e.getErrorCode().getMessage(),
                e.getErrorCode().getStatus()
        );
        return ResponseEntity.status(errorResponse.status()).body(ApiResponse.error(errorResponse));
    }


    /**
     * 에러 응답 DTO
     */
    public record ErrorResponse(
            String code,
            String message,
            HttpStatus status
    ) {}
}