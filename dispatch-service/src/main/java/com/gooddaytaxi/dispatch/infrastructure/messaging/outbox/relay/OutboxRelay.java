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
     * Outbox 패턴에서 실제 Kafka 전송(Producer) 역할을 담당하는 컴포넌트.
     *
     *  - Application은 Outbox DB에 이벤트만 기록한다. (트랜잭션 안정성)
     *  - 메시지 전송은 별도 프로세스로 분리하여 실패 시 재시도 가능하도록 함.
     *  - 따라서 KafkaProducer 클래스를 따로 두지 않고
     *    OutboxRelay가 DB → Kafka 전송을 책임진다.
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
