package com.gooddaytaxi.dispatch.infrastructure.outbox.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.dispatch.application.event.payload.DispatchRequestedPayload;
import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchRequestedCommandPort;
import org.springframework.stereotype.Component;

@Component
public class DispatchRequestedEventPublisher
        extends BaseOutboxPublisher<DispatchRequestedPayload>
        implements DispatchRequestedCommandPort {

    private static final String EVENT_TYPE = "DISPATCH_REQUESTED";
    private static final String TOPIC = "dispatch.requested";
    private static final String AGGREGATE_TYPE = "Dispatch";
    private static final int VERSION = 1;

    public DispatchRequestedEventPublisher(
            ObjectMapper mapper,
            DispatchEventOutboxPort outboxPort
    ) {
        super(mapper, outboxPort);
    }

    @Override
    public void publishRequested(DispatchRequestedPayload payload) {

        publish(
                EVENT_TYPE,
                TOPIC,
                AGGREGATE_TYPE,
                payload.notificationOriginId(),  // dispatchId (aggregateId)
                payload.driverId().toString(),   // partition key (messageKey)
                VERSION,
                payload
        );
    }
}
