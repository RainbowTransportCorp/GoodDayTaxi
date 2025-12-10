package com.gooddaytaxi.payment.application.port.out.event;

import com.gooddaytaxi.payment.application.outbox.OutboxEventModel;

import java.util.List;
import java.util.UUID;

//outbox 이벤트를 저장하는 포트
public interface PaymentEventOutboxPort {
    void save(OutboxEventModel model);
    List<OutboxEventModel> findPending(int limit);

    void markPublished(UUID eventId);
}
