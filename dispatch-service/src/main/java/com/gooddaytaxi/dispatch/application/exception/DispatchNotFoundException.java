package com.gooddaytaxi.dispatch.application.exception;

import com.gooddaytaxi.dispatch.domain.exception.DispatchErrorCode;
import com.gooddaytaxi.dispatch.domain.exception.common.DispatchDomainException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DispatchNotFoundException extends DispatchDomainException {

    private final UUID dispatchId;

    public DispatchNotFoundException(UUID dispatchId) {
        super(
            DispatchErrorCode.DISPATCH_NOT_FOUND,
            "배차 정보를 찾을 수 없습니다. dispatchId=" + dispatchId
        );
        this.dispatchId = dispatchId;
    }
}
