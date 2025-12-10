package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.dispatch.application.event.DispatchEventMetadata;
import com.gooddaytaxi.dispatch.application.event.payload.TripCreateRequestPayload;
import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import com.gooddaytaxi.dispatch.application.port.out.command.TripCreateRequestCommandPort;
import org.springframework.stereotype.Component;


@Component
public class TripCreateRequestPublisher
        extends BaseOutboxPublisher<TripCreateRequestPayload>
        implements TripCreateRequestCommandPort {

    public TripCreateRequestPublisher(
            ObjectMapper mapper,
            DispatchEventOutboxPort outboxPort
    ) {
        super(mapper, outboxPort);
    }

    @Override
    public void publishTripCreateRequest(TripCreateRequestPayload payload) {
        publish(
                DispatchEventMetadata.TRIP_CREATE_REQUEST,
                payload.dispatchId(),
                payload.dispatchId().toString(),
                payload
        );
    }
}


