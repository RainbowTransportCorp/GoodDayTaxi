package com.gooddaytaxi.dispatch.application.exception.auth;

import com.gooddaytaxi.dispatch.domain.exception.DispatchErrorCode;
import lombok.Getter;

@Getter
public class DispatchNotOwnerPassengerException extends RuntimeException {

    private final DispatchErrorCode errorCode;

    public DispatchNotOwnerPassengerException() {
        super(DispatchErrorCode.NOT_OWNER_PASSENGER.getMessage());
        this.errorCode = DispatchErrorCode.NOT_OWNER_PASSENGER;
    }
}

