package com.gooddaytaxi.dispatch.domain.exception.assignment;

import com.gooddaytaxi.dispatch.domain.exception.common.DispatchDomainException;
import com.gooddaytaxi.dispatch.domain.exception.DispatchErrorCode;

public class AssignmentAlreadyRejectedException extends DispatchDomainException {

    public AssignmentAlreadyRejectedException() {
        super(
                DispatchErrorCode.INVALID_STATE,
                "이미 REJECTED 상태인 Assignment입니다."
        );
    }
}
