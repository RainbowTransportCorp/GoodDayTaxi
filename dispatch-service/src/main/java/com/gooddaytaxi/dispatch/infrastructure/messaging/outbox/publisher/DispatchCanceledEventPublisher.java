package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.dispatch.application.event.payload.DispatchCanceledPayload;
import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCanceledCommandPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class DispatchCanceledEventPublisher
        extends BaseOutboxPublisher<DispatchCanceledPayload>
    implements DispatchCanceledCommandPort
{

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

    @Override
    public void publishCanceled(DispatchCanceledPayload payload) {
         publish(
                EVENT_TYPE,
                TOPIC,
                AGGREGATE_TYPE,
                payload.dispatchId(),
                payload.passengerId().toString(),
                VERSION,
                payload
        );
    }
}