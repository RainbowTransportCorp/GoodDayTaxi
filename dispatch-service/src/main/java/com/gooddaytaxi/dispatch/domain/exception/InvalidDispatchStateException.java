package com.gooddaytaxi.dispatch.domain.exception;

import lombok.Getter;

@Getter
public class InvalidDispatchStateException extends RuntimeException {

    private final DispatchErrorCode errorCode;

    public InvalidDispatchStateException() {
        super(DispatchErrorCode.INVALID_STATE.getMessage());
        this.errorCode = DispatchErrorCode.INVALID_STATE;
    }
}


