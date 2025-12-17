package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.dispatch.application.event.DispatchEventMetadata;
import com.gooddaytaxi.dispatch.application.event.payload.DispatchTimeoutPayload;
import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchTimeoutCommandPort;
import org.springframework.stereotype.Component;

@Component
public class DispatchTimeoutOutboxPublisher
        extends BaseOutboxPublisher<DispatchTimeoutPayload>
        implements DispatchTimeoutCommandPort {

    public DispatchTimeoutOutboxPublisher(
            ObjectMapper mapper,
            DispatchEventOutboxPort outboxPort
    ) {
        super(mapper, outboxPort);
    }

    @Override
    public void publish(DispatchTimeoutPayload payload) {
        publish(
                DispatchEventMetadata.DISPATCH_TIMEOUT,
                payload.dispatchId(),                 // aggregateId
                payload.dispatchId().toString(),      // partition key
                payload
        );
    }
}

