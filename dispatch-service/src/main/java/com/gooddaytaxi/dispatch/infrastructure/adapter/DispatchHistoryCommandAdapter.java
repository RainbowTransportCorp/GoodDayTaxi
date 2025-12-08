package com.gooddaytaxi.dispatch.infrastructure.adapter;

import com.gooddaytaxi.dispatch.application.port.out.commend.DispatchHistoryCommandPort;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DispatchHistoryCommandAdapter implements DispatchHistoryCommandPort {
    @Override
    public DispatchHistory recordStatusChange(Dispatch dispatch) {
        return null;
    }
}
