package com.gooddaytaxi.dispatch.domain.exception;

import lombok.Getter;

@Getter
public class DriverUnavailableException extends RuntimeException {

    private final DispatchErrorCode errorCode;

    public DriverUnavailableException() {
        super(DispatchErrorCode.DRIVER_UNAVAILABLE.getMessage());
        this.errorCode = DispatchErrorCode.DRIVER_UNAVAILABLE;
    }
}
