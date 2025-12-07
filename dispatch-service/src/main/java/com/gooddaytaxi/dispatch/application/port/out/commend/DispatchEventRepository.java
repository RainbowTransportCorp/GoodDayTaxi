package com.gooddaytaxi.dispatch.application.port.out.commend;

import com.gooddaytaxi.dispatch.infrastructure.outbox.entity.DispatchEvent;

public interface DispatchEventRepository {
    DispatchEvent save(DispatchEvent event);
}
