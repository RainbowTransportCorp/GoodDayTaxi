package com.gooddaytaxi.dispatch.presentation.external.exception;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.dispatch.application.exception.DispatchNotFoundException;
import com.gooddaytaxi.dispatch.application.exception.ErrorResponse;
import com.gooddaytaxi.dispatch.domain.exception.DispatchErrorCode;
import com.gooddaytaxi.dispatch.domain.exception.InvalidDispatchStateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.gooddaytaxi.dispatch.presentation.external")
public class DispatchExceptionHandler {


    @ExceptionHandler(DispatchNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleDispatchNotFound(DispatchNotFoundException e) {

        ErrorResponse response = new ErrorResponse(
                e.getCode(),         // "DISPATCH_NOT_FOUND"
                e.getMessage()       // "배차 정보를 찾을 수 없습니다. dispatchId=..."
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(response));
    }

    @ExceptionHandler(InvalidDispatchStateException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleInvalidState(
            InvalidDispatchStateException e
    ) {
        DispatchErrorCode code = e.getErrorCode();

        HttpStatus status = HttpStatus.valueOf(code.getStatus());

        // application ErrorResponse 사용
        ErrorResponse response = new ErrorResponse(
                code.getCode(),
                code.getMessage()
        );

        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(response));
    }
}


