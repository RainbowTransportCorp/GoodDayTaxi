package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.dispatch.application.event.DispatchEventMetadata;
import com.gooddaytaxi.dispatch.application.event.payload.DispatchTripReadyErrorPayload;
import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchTripReadyErrorCommandPort;
import org.springframework.stereotype.Component;

/**
 * Trip READY 지연 감지 시
 * ERROR_DETECTED 이벤트를 Outbox 패턴으로 발행한다.
 */
@Component
public class DispatchTripReadyErrorEventPublisher
    extends BaseOutboxPublisher<DispatchTripReadyErrorPayload>
    implements DispatchTripReadyErrorCommandPort {

    public DispatchTripReadyErrorEventPublisher(
        ObjectMapper objectMapper,
        DispatchEventOutboxPort outboxPort
    ) {
        super(objectMapper, outboxPort);
    }

    @Override
    public void publish(DispatchTripReadyErrorPayload payload) {
        publish(
            DispatchEventMetadata.TRIP_READY_TIMEOUT,
            payload.dispatchId(),
            payload.dispatchId().toString(), // ordering key
            payload
        );
    }
}
