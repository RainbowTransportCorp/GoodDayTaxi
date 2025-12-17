package com.gooddaytaxi.support.adapter.in.exception;

import com.gooddaytaxi.common.core.exception.BusinessException;
import com.gooddaytaxi.common.core.exception.ErrorLevel;
import com.gooddaytaxi.support.domain.exception.SupportBusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<SupportErrorResponse> handle(SupportBusinessException e) {

        return ResponseEntity
                .status(e.getSupportErrorCode().getStatus())
                .body(SupportErrorResponse.from(e.getSupportErrorCode()));
    }
}