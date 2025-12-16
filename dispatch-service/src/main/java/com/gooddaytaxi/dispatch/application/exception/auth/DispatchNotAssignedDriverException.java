package com.gooddaytaxi.dispatch.application.exception.auth;

import com.gooddaytaxi.dispatch.domain.exception.DispatchErrorCode;
import lombok.Getter;

@Getter
public class DispatchNotAssignedDriverException extends RuntimeException {

    private final DispatchErrorCode errorCode;

    public DispatchNotAssignedDriverException() {
        super(DispatchErrorCode.NOT_ASSIGNED_DRIVER.getMessage());
        this.errorCode = DispatchErrorCode.NOT_ASSIGNED_DRIVER;
    }
}
