package com.gooddaytaxi.dispatch.infrastructure.adapter;

import com.gooddaytaxi.dispatch.application.port.out.commend.DispatchAssignmentLogCommandPort;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchAssignmentLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DispatchAssignmentLogCommandAdapter implements DispatchAssignmentLogCommandPort {
    @Override
    public DispatchAssignmentLog createAssignmentLog(Dispatch dispatch) {
        return null;
    }
}
