package com.gooddaytaxi.support.domain.exception;

import com.gooddaytaxi.common.core.exception.BusinessException;
import lombok.Getter;

/**
 * Support 서비스용 BusinessException
 */
@Getter
public class SupportBusinessException extends RuntimeException {

    private final SupportErrorCode supportErrorCode;

    public SupportBusinessException(SupportErrorCode errorCode) {
        super(errorCode.getMessage());
        this.supportErrorCode = errorCode;
    }

    public SupportBusinessException(SupportErrorCode errorCode, Object... args) {
        super(String.format(errorCode.getMessage(), args));
        this.supportErrorCode = errorCode;
    }

    public SupportBusinessException(SupportErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.supportErrorCode = errorCode;
    }

    public SupportBusinessException(SupportErrorCode errorCode, Throwable cause, Object... args) {
        super(String.format(errorCode.getMessage(), args), cause);
        this.supportErrorCode = errorCode;
    }
}
