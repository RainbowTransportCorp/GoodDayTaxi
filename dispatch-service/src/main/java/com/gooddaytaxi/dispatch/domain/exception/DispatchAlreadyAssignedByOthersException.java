package com.gooddaytaxi.dispatch.domain.exception;

import lombok.Getter;

@Getter
public class DispatchAlreadyAssignedByOthersException extends RuntimeException {

    private final DispatchErrorCode errorCode;

    public DispatchAlreadyAssignedByOthersException() {
        super(DispatchErrorCode.ALREADY_ACCEPTED.getMessage());
        this.errorCode = DispatchErrorCode.ALREADY_ACCEPTED;
    }
}
