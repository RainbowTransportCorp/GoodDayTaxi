package com.gooddaytaxi.dispatch.domain.exception.dispatch;

import com.gooddaytaxi.dispatch.domain.exception.DispatchErrorCode;
import lombok.Getter;

@Getter
public class DispatchCannotCancelException extends RuntimeException {

    private final DispatchErrorCode errorCode;

    public DispatchCannotCancelException() {
        super(DispatchErrorCode.CANNOT_CANCEL.getMessage());
        this.errorCode = DispatchErrorCode.CANNOT_CANCEL;
    }
}
