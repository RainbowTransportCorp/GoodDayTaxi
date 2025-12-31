package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.dispatch.application.event.DispatchEventMetadata;
import com.gooddaytaxi.dispatch.application.event.payload.DispatchForceTimeoutPayload;
import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchForceTimeoutCommandPort;
import org.springframework.stereotype.Component;

@Component
public class DispatchForceTimeoutOutboxPublisher
    extends BaseOutboxPublisher<DispatchForceTimeoutPayload>
    implements DispatchForceTimeoutCommandPort {

    public DispatchForceTimeoutOutboxPublisher(
        ObjectMapper mapper,
        DispatchEventOutboxPort outboxPort
    ) {
        super(mapper, outboxPort);
    }

    @Override
    public void publish(DispatchForceTimeoutPayload payload) {
        publish(
            DispatchEventMetadata.DISPATCH_FORCE_TIMEOUT,
            payload.dispatchId(),            // aggregateId
            payload.dispatchId().toString(), // partition key
            payload
        );
    }
}
