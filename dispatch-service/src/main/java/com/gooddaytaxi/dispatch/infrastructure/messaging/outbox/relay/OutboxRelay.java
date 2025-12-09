package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.relay;

import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import com.gooddaytaxi.dispatch.application.outbox.OutboxEventModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelay {

    private final DispatchEventOutboxPort outboxPort;
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 일정 주기로 PENDING 상태의 Outbox Event 를 Polling 하여 Kafka로 전송하고,
     * 성공 시 PUBLISHED 로 변경한다.
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
                ).get(); // 동기 전송 → 실패 시 예외 발생

                outboxPort.markPublished(event.eventId());

                log.info("[DISPATCH-OUTBOX-RELAYED] id={} topic={} eventType={}",
                        event.aggregateId(), event.topic(), event.eventType());

            } catch (Exception ex) {
                log.error("[DISPATCH-OUTBOX-ERROR] id={} topic={} error={}",
                        event.aggregateId(), event.topic(), ex.getMessage());
            }
        }
    }
}
