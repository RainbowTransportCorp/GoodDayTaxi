package com.gooddaytaxi.dispatch.application.port.out.commend;

import com.gooddaytaxi.dispatch.domain.model.entity.DispatchAssignmentLog;

public interface DispatchAssignmentCommandPort {
    DispatchAssignmentLog save(DispatchAssignmentLog dispatchAssignmentLog);
}
