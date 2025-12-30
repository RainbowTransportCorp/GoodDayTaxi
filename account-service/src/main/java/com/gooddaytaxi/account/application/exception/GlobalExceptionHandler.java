package com.gooddaytaxi.account.application.exception;

import com.gooddaytaxi.account.domain.exception.AccountBusinessException;
import com.gooddaytaxi.account.domain.exception.AccountErrorCode;
import com.gooddaytaxi.account.presentation.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * AccountBusinessException 처리 (핵심)
     */
    @ExceptionHandler(AccountBusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(AccountBusinessException e) {

        AccountErrorCode code =
            e.getAccountErrorCode() != null
                ? e.getAccountErrorCode()
                : AccountErrorCode.INTERNAL_SERVER_ERROR;

        log.warn("AccountBusinessException 발생: {}", code.getCode(), e);

        return ResponseEntity
            .status(code.getStatus())
            .body(ErrorResponse.from(code));
    }

    /**
     * IllegalArgumentException → 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.from(AccountErrorCode.INVALID_INPUT_VALUE));
    }

    /**
     * Validation 예외
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ErrorResponse> handleValidation(Exception e) {
        return ResponseEntity
            .status(AccountErrorCode.INVALID_INPUT_VALUE.getStatus())
            .body(ErrorResponse.from(AccountErrorCode.INVALID_INPUT_VALUE));
    }

    /**
     * 데이터베이스 예외
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDatabase(DataAccessException e) {
        log.error("DB 예외 발생", e);
        return ResponseEntity
            .status(AccountErrorCode.DATABASE_ERROR.getStatus())
            .body(ErrorResponse.from(AccountErrorCode.DATABASE_ERROR));
    }

    /**
     * 최종 fallback
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
        log.error("Unhandled Exception", e);
        return ResponseEntity
            .status(AccountErrorCode.INTERNAL_SERVER_ERROR.getStatus())
            .body(ErrorResponse.from(AccountErrorCode.INTERNAL_SERVER_ERROR));
    }
}
