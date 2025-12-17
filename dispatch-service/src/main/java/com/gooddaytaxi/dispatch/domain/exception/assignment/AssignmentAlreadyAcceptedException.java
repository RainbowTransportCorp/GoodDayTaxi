package com.gooddaytaxi.dispatch.domain.exception.assignment;

import com.gooddaytaxi.dispatch.domain.exception.common.DispatchDomainException;
import com.gooddaytaxi.dispatch.domain.exception.DispatchErrorCode;

public class AssignmentAlreadyAcceptedException extends DispatchDomainException {

    public AssignmentAlreadyAcceptedException() {
        super(
                DispatchErrorCode.ALREADY_ACCEPTED,
                "이미 ACCEPTED 상태인 Assignment입니다."
        );
    }
}
