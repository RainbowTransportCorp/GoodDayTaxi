package com.gooddaytaxi.dispatch.application.outbox;

public interface DispatchEventOutboxPort {
    void save(OutboxEventModel event);
}
