package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.dispatch.application.event.DispatchEventMetadata;
import com.gooddaytaxi.dispatch.application.event.payload.DispatchRejectedPayload;
import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchRejectedCommandPort;
import org.springframework.stereotype.Component;

@Component
public class DispatchRejectedEventPublisher
        extends BaseOutboxPublisher<DispatchRejectedPayload>
        implements DispatchRejectedCommandPort {

    public DispatchRejectedEventPublisher(
            ObjectMapper mapper,
            DispatchEventOutboxPort outboxPort
    ) {
        super(mapper, outboxPort);
    }

    @Override
    public void publishRejected(DispatchRejectedPayload payload) {
        publish(
                DispatchEventMetadata.DISPATCH_REJECTED,
                payload.dispatchId(),
                payload.driverId().toString(),
                payload
        );
    }
}

