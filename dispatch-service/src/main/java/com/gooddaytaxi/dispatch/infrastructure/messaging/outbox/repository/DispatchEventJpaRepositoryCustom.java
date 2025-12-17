package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.repository;

import com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.entity.DispatchEvent;

import java.util.List;

public interface DispatchEventJpaRepositoryCustom {
    List<DispatchEvent> findPending(int limit);
}
