package com.gooddaytaxi.trip.infrastructure.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.trip.application.command.TripCreateRequestCommand;
import com.gooddaytaxi.trip.application.port.in.HandleTripCreateRequestUseCase;
import com.gooddaytaxi.trip.infrastructure.messaging.model.EventEnvelope;
import com.gooddaytaxi.trip.infrastructure.messaging.model.TripCreateRequestPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TripCreateRequestConsumer {

    private final HandleTripCreateRequestUseCase handleTripCreateRequestUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "trip.create.request",
            groupId = "trip-service",
            containerFactory = "tripCreateRequestKafkaListenerContainerFactory"
    )
    public void consume(EventEnvelope<?> envelope, Acknowledgment ack) {

        log.info("[TripCreateRequestConsumer] received eventId={}, type={}",
                envelope.eventId(), envelope.eventType());

        try {
            // 1️⃣ payload 변환 (제네릭 안정 패턴)
            TripCreateRequestPayload payload =
                    objectMapper.convertValue(
                            envelope.payload(),
                            TripCreateRequestPayload.class
                    );

            // 2️⃣ Command 변환
            TripCreateRequestCommand command =
                    TripCreateRequestCommand.from(payload);

            // 3️⃣ Application Layer 호출
            handleTripCreateRequestUseCase.handle(command);

            // 4️⃣ 정상 처리 → offset 커밋
            ack.acknowledge();

            log.info("[TripCreateRequestConsumer] processed successfully. dispatchId={}",
                    payload.dispatchId());

        } catch (Exception e) {
            log.error("[TripCreateRequestConsumer] processing failed. eventId={}",
                    envelope.eventId(), e);
                        throw e;
        }
    }
}
