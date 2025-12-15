package com.gooddaytaxi.dispatch.domain.exception;

import lombok.Getter;

@Getter
public class DispatchNotOwnerPassengerException extends RuntimeException {

    private final DispatchErrorCode errorCode;

    public DispatchNotOwnerPassengerException() {
        super(DispatchErrorCode.NOT_OWNER_PASSENGER.getMessage());
        this.errorCode = DispatchErrorCode.NOT_OWNER_PASSENGER;
    }
}

