package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.dispatch.application.event.DispatchEventMetadata;
import com.gooddaytaxi.dispatch.application.event.payload.DispatchCanceledPayload;
import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCanceledCommandPort;
import org.springframework.stereotype.Component;

/**
 * 배차 취소(DISPATCH_CANCELED) 이벤트를
 * Outbox 패턴을 통해 발행하는 Publisher 구현체.
 */
@Component
public class DispatchCanceledEventPublisher
        extends BaseOutboxPublisher<DispatchCanceledPayload>
        implements DispatchCanceledCommandPort {

    public DispatchCanceledEventPublisher(
            ObjectMapper objectMapper,
            DispatchEventOutboxPort outboxPort
    ) {
        super(objectMapper, outboxPort);
    }

    @Override
    public void publishCanceled(DispatchCanceledPayload payload) {
        publish(
                DispatchEventMetadata.DISPATCH_CANCELED,
                payload.dispatchId(),
                payload.passengerId().toString(),
                payload
        );
    }
}
