package com.gooddaytaxi.dispatch.application.port.out.command;

import com.gooddaytaxi.dispatch.domain.model.entity.DispatchAssignmentLog;

public interface DispatchAssignmentCommandPort {
    DispatchAssignmentLog save(DispatchAssignmentLog dispatchAssignmentLog);
}
