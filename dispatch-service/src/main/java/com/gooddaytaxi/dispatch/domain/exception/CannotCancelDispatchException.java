package com.gooddaytaxi.dispatch.domain.exception;

import lombok.Getter;

@Getter
public class CannotCancelDispatchException extends RuntimeException {

    private final DispatchErrorCode errorCode;

    public CannotCancelDispatchException() {
        super(DispatchErrorCode.CANNOT_CANCEL.getMessage());
        this.errorCode = DispatchErrorCode.CANNOT_CANCEL;
    }
}
