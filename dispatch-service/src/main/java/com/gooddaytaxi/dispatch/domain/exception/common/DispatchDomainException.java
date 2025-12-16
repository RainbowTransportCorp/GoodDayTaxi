package com.gooddaytaxi.dispatch.domain.exception.common;

import com.gooddaytaxi.dispatch.domain.exception.DispatchErrorCode;
import lombok.Getter;

@Getter
public abstract class DispatchDomainException extends RuntimeException {

    private final DispatchErrorCode errorCode;
    private final String detail;

    protected DispatchDomainException(
            DispatchErrorCode errorCode,
            String detail
    ) {
        super(detail);
        this.errorCode = errorCode;
        this.detail = detail;
    }

}
