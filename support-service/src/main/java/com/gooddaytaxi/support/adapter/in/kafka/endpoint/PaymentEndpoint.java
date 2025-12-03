package com.gooddaytaxi.support.adapter.in.kafka.endpoint;


import com.gooddaytaxi.support.application.service.PaymentNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Payment 엔드포인트 - Payment로부터 발생하는 이벤트에 대한 리스너
 */
@Component
@RequiredArgsConstructor
public class PaymentEndpoint {

    private final PaymentNotificationService service;

//    @KafkaListener(topics = "payment.completed", groupId = "support-service")
//    public void onPaymentCompleted(String message) {
//        service.handlePaymentCompletedEvent(message);
//    }
}
