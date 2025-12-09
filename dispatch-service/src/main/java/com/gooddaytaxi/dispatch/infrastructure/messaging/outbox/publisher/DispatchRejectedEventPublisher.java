package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.dispatch.application.event.payload.DispatchRejectedPayload;
import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchRejectedCommandPort;
import org.springframework.stereotype.Component;

@Component
public class DispatchRejectedEventPublisher
        extends BaseOutboxPublisher<DispatchRejectedPayload>
        implements DispatchRejectedCommandPort {

    private static final String EVENT_TYPE = "DISPATCH_REJECTED";
    private static final String TOPIC = "dispatch.rejected";
    private static final String AGGREGATE_TYPE = "Dispatch";
    private static final int VERSION = 1;

    public DispatchRejectedEventPublisher(
            ObjectMapper mapper,
            DispatchEventOutboxPort outboxPort
    ) {
        super(mapper, outboxPort);
    }

    @Override
    public void publishRejected(DispatchRejectedPayload payload) {
        publish(
                EVENT_TYPE,
                TOPIC,
                AGGREGATE_TYPE,
                payload.dispatchId(),
                payload.driverId().toString(),
                VERSION,
                payload
        );
    }
}
