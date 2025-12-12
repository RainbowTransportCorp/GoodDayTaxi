package com.gooddaytaxi.payment.presentation.external.exception;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.payment.application.exception.ErrorResponse;
import com.gooddaytaxi.payment.infrastructure.outbox.PaymentOutboxException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PaymentOutboxExceptionHandler {
    @ExceptionHandler(PaymentOutboxException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handlePaymentOutboxException(PaymentOutboxException e) {
        ErrorResponse response = new ErrorResponse(
                "PI001",         // Payment + Infrastructure Error Code
                e.getMessage()       // "Outbox payload serialization failed"
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(response));
    }
}
