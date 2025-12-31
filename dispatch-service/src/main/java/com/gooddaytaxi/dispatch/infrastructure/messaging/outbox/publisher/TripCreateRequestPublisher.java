package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.dispatch.application.event.DispatchEventMetadata;
import com.gooddaytaxi.dispatch.application.event.payload.TripCreateRequestPayload;
import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import com.gooddaytaxi.dispatch.application.port.out.command.TripCreateRequestCommandPort;
import org.springframework.stereotype.Component;

/**
 * 배차 수락 이후 Trip 서비스에
 * 운행 생성을 요청하는 이벤트를 Outbox로 발행하는 Publisher.
 */
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

    /**
     * 배차 수락 이후, 운행 생성을 요청하는 이벤트를 발행한다.
     * @param payload 시작될 운행 정보가 담긴 payload
     */
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


