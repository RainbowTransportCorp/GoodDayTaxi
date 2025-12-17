package com.gooddaytaxi.dispatch.domain.exception.dispatch;

import com.gooddaytaxi.dispatch.domain.exception.DispatchErrorCode;
import lombok.Getter;

@Getter
public class DispatchAlreadyAssignedByOthersException extends RuntimeException {

    private final DispatchErrorCode errorCode;

    public DispatchAlreadyAssignedByOthersException() {
        super(DispatchErrorCode.ALREADY_ACCEPTED.getMessage());
        this.errorCode = DispatchErrorCode.ALREADY_ACCEPTED;
    }
}
