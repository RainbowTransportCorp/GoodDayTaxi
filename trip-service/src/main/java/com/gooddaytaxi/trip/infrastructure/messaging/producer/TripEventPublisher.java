package com.gooddaytaxi.trip.infrastructure.messaging.producer;

import com.gooddaytaxi.trip.domain.model.enums.TripEventType;
import com.gooddaytaxi.trip.infrastructure.messaging.outbox.entity.TripEventOutbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class TripEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishSync(TripEventOutbox event) {
        String topic = resolveTopic(event.getEventType());
        String key = event.getTripId().toString();
        String value = event.getPayload();

        try {
            SendResult<String, String> result =
                    kafkaTemplate.send(topic, key, value).get(5, TimeUnit.SECONDS);

            log.info("Trip event published. eventId={}, topic={}, partition={}, offset={}",
                    event.getEventId(),
                    result.getRecordMetadata().topic(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset()
            );
        } catch (Exception e) {
            log.error("Failed to publish trip event. eventId={}, topic={}",
                    event.getEventId(), topic, e);
            throw new RuntimeException(e);
        }
    }

    private String resolveTopic(TripEventType eventType) {
        return switch (eventType) {
            case TRIP_CREATED -> "trip.created";
            case TRIP_STARTED -> "trip.started";
            case TRIP_ENDED -> "trip.ended";
            case TRIP_CANCELED -> "trip.canceled";
            default -> "trip.events";
        };
    }
}
