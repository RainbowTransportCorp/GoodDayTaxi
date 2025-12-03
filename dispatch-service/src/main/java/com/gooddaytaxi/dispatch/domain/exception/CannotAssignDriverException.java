package com.gooddaytaxi.dispatch.domain.exception;

import lombok.Getter;

@Getter
public class CannotAssignDriverException extends RuntimeException {

    private final DispatchErrorCode errorCode;

    public CannotAssignDriverException() {
        super(DispatchErrorCode.CANNOT_ASSIGN.getMessage());
        this.errorCode = DispatchErrorCode.CANNOT_ASSIGN;
    }
}
