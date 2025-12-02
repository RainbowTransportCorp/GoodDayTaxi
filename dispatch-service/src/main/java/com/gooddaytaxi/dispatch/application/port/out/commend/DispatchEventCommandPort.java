package com.gooddaytaxi.dispatch.application.port.out.commend;

import com.gooddaytaxi.dispatch.domain.model.entity.DispatchEvent;

public interface DispatchEventCommandPort {
    void save(DispatchEvent event);
}
