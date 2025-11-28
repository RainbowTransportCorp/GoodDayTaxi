package com.gooddaytaxi.dispatch.application.port.out.commend;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;

public interface DispatchCommandPort {

    Dispatch save(Dispatch request);
}
