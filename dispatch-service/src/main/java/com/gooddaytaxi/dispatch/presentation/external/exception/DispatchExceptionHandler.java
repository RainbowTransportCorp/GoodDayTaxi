package com.gooddaytaxi.dispatch.presentation.external.exception;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.dispatch.application.exception.ErrorResponse;
import com.gooddaytaxi.dispatch.domain.exception.DispatchErrorCode;
import com.gooddaytaxi.dispatch.domain.exception.InvalidDispatchStateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class DispatchExceptionHandler {

    @ExceptionHandler(InvalidDispatchStateException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleInvalidState(
            InvalidDispatchStateException e
    ) {
        DispatchErrorCode code = e.getErrorCode();

        HttpStatus status = HttpStatus.valueOf(code.getStatus());

        ErrorResponse response = new ErrorResponse(
                code.getCode(),
                code.getMessage()
        );

        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(response));
    }
}


