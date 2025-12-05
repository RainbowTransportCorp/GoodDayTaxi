package com.gooddaytaxi.account.application.exception;

import com.gooddaytaxi.account.domain.exception.AccountBusinessException;
import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.common.core.exception.BusinessException;
import com.gooddaytaxi.common.core.exception.ErrorLevel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 핸들러
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * AccountBusinessException 처리 (Account 도메인 특화)
     *
     * @param e Account 비즈니스 예외
     * @return ApiResponse와 적절한 HTTP 상태 코드
     */
    @ExceptionHandler(AccountBusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handle(AccountBusinessException e) {

        HttpStatus status = mapErrorLevelToHttpStatus(e.getAccountErrorCode().getLevel());

        return ResponseEntity
                .status(status)
                .body(ApiResponse.success(null, e.getAccountErrorCode().getMessage()));
    }

    /**
     * BusinessException 처리 (공통 에러)
     *
     * @param e 비즈니스 예외
     * @return ApiResponse와 적절한 HTTP 상태 코드
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handle(BusinessException e) {

        HttpStatus status = mapErrorLevelToHttpStatus(e.getErrorCode().getLevel());

        return ResponseEntity
                .status(status)
                .body(ApiResponse.success(null, e.getErrorCode().getMessage()));
    }
    
    /**
     * Validation 예외 처리
     *
     * @param e 검증 예외
     * @return ApiResponse와 BAD_REQUEST 상태 코드
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiResponse<Void>> handleValidationException(Exception e) {
        String message = "입력 값이 올바르지 않습니다.";
        
        if (e instanceof MethodArgumentNotValidException validException) {
            if (validException.getBindingResult().hasFieldErrors()) {
                message = validException.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
            }
        } else if (e instanceof BindException bindException) {
            if (bindException.getBindingResult().hasFieldErrors()) {
                message = bindException.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
            }
        }
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.success(null, message));
    }
    
    /**
     * 일반 예외 처리
     *
     * @param e 일반 예외
     * @return ApiResponse와 INTERNAL_SERVER_ERROR 상태 코드
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.success(null, "서버 내부 오류가 발생했습니다."));
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
