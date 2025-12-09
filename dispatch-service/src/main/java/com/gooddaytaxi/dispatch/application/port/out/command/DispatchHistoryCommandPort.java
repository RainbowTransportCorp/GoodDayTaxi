package com.gooddaytaxi.dispatch.application.port.out.command;

import com.gooddaytaxi.dispatch.domain.model.entity.DispatchHistory;

public interface DispatchHistoryCommandPort {
    DispatchHistory save(DispatchHistory history);
}