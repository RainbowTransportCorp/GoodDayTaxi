package com.gooddaytaxi.dispatch.infrastructure.outbox.publisher;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.dispatch.application.event.payload.DispatchAcceptedPayload;
import com.gooddaytaxi.dispatch.application.port.out.commend.DispatchEventRepository;
import org.springframework.stereotype.Component;

@Component
public class DispatchAcceptedEventPublisher
        extends BaseOutboxPublisher<DispatchAcceptedPayload> {

    private static final String EVENT_TYPE = "DISPATCH_ACCEPTED";
    private static final String TOPIC = "dispatch.accepted";
    private static final String AGGREGATE_TYPE = "Dispatch";
    private static final int VERSION = 1;

    public DispatchAcceptedEventPublisher(ObjectMapper mapper,
                                          DispatchEventRepository repo) {
        super(mapper, repo);
    }

    public void save(DispatchAcceptedPayload payload) {
        publish(
                EVENT_TYPE,
                TOPIC,
                AGGREGATE_TYPE,
                payload.dispatchId(),
                payload.passengerId().toString(), // messageKey
                VERSION,
                payload
        );
    }
}
