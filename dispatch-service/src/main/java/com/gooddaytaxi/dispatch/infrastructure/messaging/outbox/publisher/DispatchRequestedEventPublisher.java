package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.dispatch.application.event.DispatchEventMetadata;
import com.gooddaytaxi.dispatch.application.event.payload.DispatchRequestedPayload;
import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchRequestedCommandPort;
import org.springframework.stereotype.Component;

@Component
public class DispatchRequestedEventPublisher
        extends BaseOutboxPublisher<DispatchRequestedPayload>
        implements DispatchRequestedCommandPort {

    public DispatchRequestedEventPublisher(
            ObjectMapper mapper,
            DispatchEventOutboxPort outboxPort
    ) {
        super(mapper, outboxPort);
    }

    @Override
    public void publishRequested(DispatchRequestedPayload payload) {
        publish(
                DispatchEventMetadata.DISPATCH_REQUESTED,
                payload.notificationOriginId(),    // dispatchId
                payload.driverId().toString(),
                payload
        );
    }
}

