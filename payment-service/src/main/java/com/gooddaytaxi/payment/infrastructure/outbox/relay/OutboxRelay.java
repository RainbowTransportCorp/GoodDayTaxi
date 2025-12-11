package com.gooddaytaxi.payment.infrastructure.outbox.relay;

import com.gooddaytaxi.payment.application.outbox.OutboxEventModel;
import com.gooddaytaxi.payment.application.port.out.event.PaymentEventOutboxPort;
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

    private final PaymentEventOutboxPort outboxPort;
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

                //해당 이벤트를 찾아 published로 상태 변경 + publishedAt 업데이트
                outboxPort.markPublished(event.eventId());

                log.info("[PAYMENT-OUTBOX-RELAYED] id={} topic={} eventType={}",
                        event.eventId(), event.topic(), event.eventType());

            } catch (Exception ex) {
                log.error("[PAYMENT-OUTBOX-ERROR] id={} topic={} error={}",
                        event.eventId(), event.topic(), ex.getMessage());
            }
        }
    }
}