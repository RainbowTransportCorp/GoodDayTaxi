package com.gooddaytaxi.account.application.exception;

import com.gooddaytaxi.account.domain.exception.AccountBusinessException;
import com.gooddaytaxi.account.domain.exception.AccountErrorCode;
import com.gooddaytaxi.account.presentation.dto.ErrorResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 핸들러 
 * dispatch 서비스와 동일한 패턴으로 통일
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * AccountBusinessException 처리
     */
    @ExceptionHandler(AccountBusinessException.class)
    public ResponseEntity<ErrorResponse> handle(AccountBusinessException e) {
        return ResponseEntity
                .status(e.getAccountErrorCode().getStatus())
                .body(ErrorResponse.from(e.getAccountErrorCode()));
    }

    /**
     * Validation 예외 처리
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ErrorResponse> handleValidation(Exception e) {
        return ResponseEntity
                .status(AccountErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(ErrorResponse.from(AccountErrorCode.INVALID_INPUT_VALUE));
    }

    /**
     * 데이터베이스 예외 처리
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDatabase(DataAccessException e) {
        return ResponseEntity
                .status(AccountErrorCode.DATABASE_ERROR.getStatus())
                .body(ErrorResponse.from(AccountErrorCode.DATABASE_ERROR));
    }

    /**
     * 일반 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
        return ResponseEntity
                .status(AccountErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ErrorResponse.from(AccountErrorCode.INTERNAL_SERVER_ERROR));
    }
}
