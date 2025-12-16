package com.gooddaytaxi.dispatch.domain.exception.assignment;

import com.gooddaytaxi.dispatch.domain.exception.common.DispatchDomainException;
import com.gooddaytaxi.dispatch.domain.exception.DispatchErrorCode;
import com.gooddaytaxi.dispatch.domain.model.enums.AssignmentStatus;

public class AssignmentInvalidStateException extends DispatchDomainException {

    public AssignmentInvalidStateException(
            AssignmentStatus currentStatus,
            String action
    ) {
        super(
                DispatchErrorCode.INVALID_STATE,
                action + "은(는) SENT 상태에서만 가능합니다. 현재 상태=" + currentStatus
        );
    }
}
