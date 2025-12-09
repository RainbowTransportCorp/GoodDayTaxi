package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.dispatch.application.event.payload.DispatchCancelledPayload;
import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class DispatchCanceledEventPublisher
        extends BaseOutboxPublisher<DispatchCancelledPayload> {

    private static final String EVENT_TYPE = "DISPATCH_CANCELLED";
    private static final String TOPIC = "dispatch.cancelled";
    private static final String AGGREGATE_TYPE = "Dispatch";
    private static final int VERSION = 1;

    public DispatchCanceledEventPublisher(
            ObjectMapper objectMapper,
            DispatchEventOutboxPort outboxPort
    ) {
        super(objectMapper, outboxPort);
    }

    public void publishCancelEvent(UUID dispatchId, DispatchCancelledPayload payload) {

        super.publish(
                EVENT_TYPE,
                TOPIC,
                AGGREGATE_TYPE,
                dispatchId,
                dispatchId.toString(),  // messageKey = dispatchId
                VERSION,
                payload
        );
    }
}