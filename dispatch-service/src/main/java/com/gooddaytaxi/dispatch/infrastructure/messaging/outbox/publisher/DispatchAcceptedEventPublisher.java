package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.dispatch.application.event.DispatchEventMetadata;
import com.gooddaytaxi.dispatch.application.event.payload.DispatchAcceptedPayload;
import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchAcceptedCommandPort;
import org.springframework.stereotype.Component;

/**
 * 배차 수락(DISPATCH_ACCEPTED) 이벤트를
 * Outbox 패턴을 통해 발행하는 Publisher 구현체.
 *
 * DispatchAcceptedCommandPort를 구현하며,
 * 실제 이벤트 저장 및 발행 책임은 BaseOutboxPublisher에 위임한다.
 */
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

    /**
     * 배차 수락 이벤트를 Outbox에 발행한다.
     * @param payload 수락된 배차정보가 담긴 payload
     */
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

