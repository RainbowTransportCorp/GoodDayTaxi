package com.gooddaytaxi.dispatch.domain.exception.assignment;

import com.gooddaytaxi.dispatch.domain.exception.common.DispatchDomainException;
import com.gooddaytaxi.dispatch.domain.exception.DispatchErrorCode;

import java.util.UUID;

public class AssignmentLogNotFoundException extends DispatchDomainException {

    public AssignmentLogNotFoundException(UUID dispatchId, UUID driverId) {
        super(
                DispatchErrorCode.INVALID_STATE,
                "해당 기사에게 발송된 AssignmentLog가 존재하지 않습니다. " +
                        "dispatchId=" + dispatchId + ", driverId=" + driverId
        );
    }
}
