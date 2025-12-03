package com.gooddaytaxi.support.adapter.in.kafka.endpoint;

import com.gooddaytaxi.support.application.service.TripNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Trip 엔드포인트 - Trip으로부터 발생하는 이벤트에 대한 리스너
 */
@Component
@RequiredArgsConstructor
public class TripEndpoint {

    private final TripNotificationService service;

    @KafkaListener(topics = "trip.started", groupId = "support-service")
    public void onTripStarted(String message) {
        service.handleTripStartedEvent(message);
    }

    @KafkaListener(topics = "trip.completed", groupId = "support-service")
    public void onTripCompleted(String message) {
        service.handleTripCompletedEvent(message);
    }
}
