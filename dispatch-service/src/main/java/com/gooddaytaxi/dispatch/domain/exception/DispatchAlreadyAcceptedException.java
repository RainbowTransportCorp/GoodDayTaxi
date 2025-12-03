package com.gooddaytaxi.dispatch.domain.exception;

import lombok.Getter;

@Getter
public class DispatchAlreadyAcceptedException extends RuntimeException {

    private final DispatchErrorCode errorCode;

    public DispatchAlreadyAcceptedException() {
        super(DispatchErrorCode.ALREADY_ACCEPTED.getMessage());
        this.errorCode = DispatchErrorCode.ALREADY_ACCEPTED;
    }
}
