package com.gooddaytaxi.support.adapter.in.kafka.endpoint;


import com.gooddaytaxi.support.application.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * SystemError 이벤트 리스너
 */
@Component
@RequiredArgsConstructor
class SystemErrorEventListener {

    private final LogService service;

    @KafkaListener(topics = "system.error-domain-events", groupId = "support-service")
    public void onErrorEvent(String message) {
        service.handleErrorDomainEvent(message);
    }
}
