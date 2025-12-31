package com.gooddaytaxi.dispatch.application.exception;

import com.gooddaytaxi.dispatch.domain.exception.DispatchErrorCode;
import lombok.Getter;

@Getter
public class DriverUnavailableException extends RuntimeException {

    private final DispatchErrorCode errorCode;

    public DriverUnavailableException() {
        super(DispatchErrorCode.DRIVER_UNAVAILABLE.getMessage());
        this.errorCode = DispatchErrorCode.DRIVER_UNAVAILABLE;
    }
}
