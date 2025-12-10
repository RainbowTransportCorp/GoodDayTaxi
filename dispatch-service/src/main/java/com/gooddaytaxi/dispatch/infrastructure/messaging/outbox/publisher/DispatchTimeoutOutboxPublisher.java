package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.dispatch.application.event.payload.DispatchTimeoutPayload;
import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DispatchTimeoutOutboxPublisher
        extends BaseOutboxPublisher<DispatchTimeoutPayload> {

    private static final String EVENT_TYPE = "DISPATCH_TIMEOUT";
    private static final String TOPIC = "dispatch.timeout";
    private static final String AGGREGATE_TYPE = "Dispatch";
    private static final int VERSION = 1;

    public DispatchTimeoutOutboxPublisher(
            ObjectMapper objectMapper,
            DispatchEventOutboxPort outboxPort
    ) {
        super(objectMapper, outboxPort);
    }

    public void publishTimeout(DispatchTimeoutPayload payload) {
        publish(
                EVENT_TYPE,
                TOPIC,
                AGGREGATE_TYPE,
                payload.dispatchId(),
                payload.dispatchId().toString(),
                VERSION,
                payload
        );
    }
}