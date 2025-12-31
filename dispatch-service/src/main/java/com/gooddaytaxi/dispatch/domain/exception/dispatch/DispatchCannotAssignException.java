package com.gooddaytaxi.dispatch.domain.exception.dispatch;

import com.gooddaytaxi.dispatch.domain.exception.DispatchErrorCode;
import com.gooddaytaxi.dispatch.domain.exception.common.DispatchDomainException;

public class DispatchCannotAssignException extends DispatchDomainException {

    public DispatchCannotAssignException() {
        super(
                DispatchErrorCode.CANNOT_ASSIGN,
                "현재 상태에서는 기사에게 배정할 수 없습니다."
        );
    }

    public DispatchCannotAssignException(String detail) {
        super(
                DispatchErrorCode.CANNOT_ASSIGN,
                detail
        );
    }
}
