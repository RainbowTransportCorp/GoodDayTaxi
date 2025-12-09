package com.gooddaytaxi.dispatch.application.outbox;

import java.util.List;
import java.util.UUID;

public interface DispatchEventOutboxPort {
    void save(OutboxEventModel event);
    List<OutboxEventModel> findPending(int limit);
    void markPublished(UUID eventId);
}
