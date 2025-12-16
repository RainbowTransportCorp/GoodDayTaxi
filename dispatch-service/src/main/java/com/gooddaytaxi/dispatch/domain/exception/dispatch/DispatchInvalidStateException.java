package com.gooddaytaxi.dispatch.domain.exception.dispatch;

import com.gooddaytaxi.dispatch.domain.exception.DispatchErrorCode;
import com.gooddaytaxi.dispatch.domain.exception.common.DispatchDomainException;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;

public class DispatchInvalidStateException extends DispatchDomainException {

    private DispatchInvalidStateException(String detail) {
        super(DispatchErrorCode.INVALID_STATE, detail);
    }

    public static DispatchInvalidStateException cannot(
            DispatchStatus current,
            String action
    ) {
        return new DispatchInvalidStateException(
                "현재 배차 상태(" + current + ")에서는 " + action + "을(를) 수행할 수 없습니다."
        );
    }
}
