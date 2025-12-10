package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.dispatch.application.event.DispatchEventMetadata;
import com.gooddaytaxi.dispatch.application.event.payload.DispatchTimeoutPayload;
import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import org.springframework.stereotype.Component;

@Component
public class DispatchTimeoutOutboxPublisher
        extends BaseOutboxPublisher<DispatchTimeoutPayload> {

    public DispatchTimeoutOutboxPublisher(
            ObjectMapper mapper,
            DispatchEventOutboxPort outboxPort
    ) {
        super(mapper, outboxPort);
    }

    public void publishTimeout(DispatchTimeoutPayload payload) {
        publish(
                DispatchEventMetadata.DISPATCH_TIMEOUT,
                payload.dispatchId(),
                payload.dispatchId().toString(),
                payload
        );
    }
}
