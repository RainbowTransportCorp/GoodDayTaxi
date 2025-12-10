package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.dispatch.application.event.DispatchEventMetadata;
import com.gooddaytaxi.dispatch.application.event.payload.DispatchAcceptedPayload;
import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchAcceptedCommandPort;
import org.springframework.stereotype.Component;

@Component
public class DispatchAcceptedEventPublisher
        extends BaseOutboxPublisher<DispatchAcceptedPayload>
        implements DispatchAcceptedCommandPort {

    public DispatchAcceptedEventPublisher(
            ObjectMapper mapper,
            DispatchEventOutboxPort outboxPort
    ) {
        super(mapper, outboxPort);
    }

    @Override
    public void publishAccepted(DispatchAcceptedPayload payload) {
        publish(
                DispatchEventMetadata.DISPATCH_ACCEPTED,
                payload.dispatchId(),
                payload.passengerId().toString(),
                payload
        );
    }
}

