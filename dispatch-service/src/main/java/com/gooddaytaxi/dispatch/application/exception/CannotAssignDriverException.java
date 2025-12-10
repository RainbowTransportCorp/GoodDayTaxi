package com.gooddaytaxi.dispatch.application.exception;

import com.gooddaytaxi.dispatch.domain.exception.DispatchErrorCode;
import lombok.Getter;

@Getter
public class CannotAssignDriverException extends RuntimeException {

    private final DispatchErrorCode errorCode;

    public CannotAssignDriverException() {
        super(DispatchErrorCode.CANNOT_ASSIGN.getMessage());
        this.errorCode = DispatchErrorCode.CANNOT_ASSIGN;
    }
}
