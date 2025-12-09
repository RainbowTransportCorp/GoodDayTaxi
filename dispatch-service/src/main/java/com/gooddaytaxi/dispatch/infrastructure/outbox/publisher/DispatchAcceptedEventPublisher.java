package com.gooddaytaxi.dispatch.infrastructure.outbox.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.dispatch.application.event.payload.DispatchAcceptedPayload;
import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchAcceptedCommandPort;
import org.springframework.stereotype.Component;

@Component
public class DispatchAcceptedEventPublisher
        extends BaseOutboxPublisher<DispatchAcceptedPayload>
        implements DispatchAcceptedCommandPort {

    private static final String EVENT_TYPE = "DISPATCH_ACCEPTED";
    private static final String TOPIC = "dispatch.accepted";
    private static final String AGGREGATE_TYPE = "Dispatch";
    private static final int VERSION = 1;

    public DispatchAcceptedEventPublisher(
            ObjectMapper mapper,
            DispatchEventOutboxPort outboxPort
    ) {
        super(mapper, outboxPort);
    }

    @Override
    public void publishAccepted(DispatchAcceptedPayload payload) {
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

