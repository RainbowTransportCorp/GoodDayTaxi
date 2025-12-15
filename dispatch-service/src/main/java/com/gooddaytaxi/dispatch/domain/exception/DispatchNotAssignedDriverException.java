package com.gooddaytaxi.dispatch.domain.exception;

import lombok.Getter;

@Getter
public class DispatchNotAssignedDriverException extends RuntimeException {

    private final DispatchErrorCode errorCode;

    public DispatchNotAssignedDriverException() {
        super(DispatchErrorCode.NOT_ASSIGNED_DRIVER.getMessage());
        this.errorCode = DispatchErrorCode.NOT_ASSIGNED_DRIVER;
    }
}
