package com.gooddaytaxi.dispatch.application.port.out.commend;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchAssignmentLog;

public interface DispatchAssignmentLogCommandPort {
    DispatchAssignmentLog createAssignmentLog(Dispatch dispatch);
}
