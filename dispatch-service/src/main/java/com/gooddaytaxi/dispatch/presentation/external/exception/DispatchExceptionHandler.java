package com.gooddaytaxi.dispatch.presentation.external.exception;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.dispatch.application.exception.DispatchNotFoundException;
import com.gooddaytaxi.dispatch.application.exception.DispatchPermissionDeniedException;
import com.gooddaytaxi.dispatch.application.exception.ErrorResponse;
import com.gooddaytaxi.dispatch.domain.exception.DispatchErrorCode;
import com.gooddaytaxi.dispatch.domain.exception.common.DispatchDomainException;
import com.gooddaytaxi.dispatch.presentation.external.controller.AdminDispatchController;
import com.gooddaytaxi.dispatch.presentation.external.controller.DispatchController;
import com.gooddaytaxi.dispatch.presentation.external.controller.DriverDispatchController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {
        DispatchController.class,
        DriverDispatchController.class,
        AdminDispatchController.class
})
public class DispatchExceptionHandler {

    // 도메인 예외는 싹 여기서 처리
    @ExceptionHandler(DispatchDomainException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleDomainException(DispatchDomainException e) {

        DispatchErrorCode code = e.getErrorCode();
        HttpStatus status = HttpStatus.valueOf(code.getStatus());

        // detail을 내려주자 (super(detail))
        ErrorResponse response = new ErrorResponse(
                code.getCode(),
                e.getMessage()
        );

        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(response));
    }

    @ExceptionHandler(DispatchPermissionDeniedException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handlePermissionDenied(
            DispatchPermissionDeniedException e
    ) {
        ErrorResponse response = new ErrorResponse(
                e.getCode(),
                e.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(response));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleUnknown(Exception e) {

        ErrorResponse response = new ErrorResponse(
                "INTERNAL_ERROR",
                "서버 내부 오류가 발생했습니다."
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(response));
    }

}
