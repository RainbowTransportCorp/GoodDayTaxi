package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.relay;

import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import com.gooddaytaxi.dispatch.application.outbox.OutboxEventModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Outbox 테이블에 저장된 이벤트를 조회하여
 * Kafka로 실제 전송하는 Relay 컴포넌트.
 *
 * Application 계층은 트랜잭션 안정성을 위해
 * 이벤트를 Outbox DB에만 기록하고,
 * 이 클래스가 별도의 흐름으로 DB → Kafka 전송을 담당한다.
 *
 * 전송 실패 시 이벤트는 Outbox에 남아 재시도된다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelay {

    private final DispatchEventOutboxPort outboxPort;
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Outbox에 저장된 미전송 이벤트를 조회하여 Kafka로 전송한다.
     */
    @Scheduled(fixedDelayString = "${outbox.relay.delay-ms:5000}")
    public void relay() {
        List<OutboxEventModel> pending = outboxPort.findPending(100);

        for (OutboxEventModel event : pending) {
            try {
                kafkaTemplate.send(
                        event.topic(),
                        event.messageKey(),
                        event.payloadJson()
                ).get();

                outboxPort.markPublished(event.eventId());
            } catch (Exception ex) {
                log.error("[DISPATCH-OUTBOX-ERROR] id={} topic={} error={}",
                        event.eventId(), event.topic(), ex.getMessage());
            }
        }
    }

}
