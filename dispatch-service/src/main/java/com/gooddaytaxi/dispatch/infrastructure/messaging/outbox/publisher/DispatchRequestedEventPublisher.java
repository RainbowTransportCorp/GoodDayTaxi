package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.dispatch.application.event.DispatchEventMetadata;
import com.gooddaytaxi.dispatch.application.event.payload.DispatchRequestedPayload;
import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchRequestedCommandPort;
import org.springframework.stereotype.Component;

/**
 * DispatchRequested 이벤트는
 * 배차 도메인의 상태 전이를 표현하기 위한 이벤트가 아니라,
 * Support(알림) 컨텍스트에서 기사에게 "배차 요청이 도착했음"을
 * 알리기 위한 트리거 이벤트이다.
 *
 * 이 이벤트는 특정 기사가 실제로 배정되었음을 의미하지 않으며,
 * 수락/거절 여부에 대한 결정 또한 포함하지 않는다.
 *
 * 이벤트 이름이 다소 포괄적이고 모호한 이유는,
 * 알림 컨텍스트에서 필요한 최소한의 의미만 전달하고
 * 배차 도메인의 내부 결정 로직이나 상태 의미를
 * 외부로 노출하지 않기 위함이다.
 */

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

    /**
     * 기사에게 배차 요청 알림을 전달하기 위한 이벤트 발행 (응답 유도용 알림)
     * @param payload 승객이 요청한 출발지와 도착지 등의 정보가 담긴 페이로드
     */
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

