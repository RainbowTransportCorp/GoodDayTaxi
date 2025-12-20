package com.gooddaytaxi.payment.infrastructure.outbox.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.payment.application.command.payment.PaymentCreateCommand;
import com.gooddaytaxi.payment.application.event.EventEnvelope;
import com.gooddaytaxi.payment.application.event.TripEndedPayload;
import com.gooddaytaxi.payment.application.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TripEndedEventConsumer {
    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;

    @KafkaListener(
            topics = "trip.ended",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(EventEnvelope<?> envelope, Acknowledgment ack) {
        log.info("TripEndedConsumer start processing. eventId={}, eventType={}",envelope.eventId(),envelope.eventType());
        try {
        //payload 변환
        TripEndedPayload payload =
                objectMapper.convertValue(envelope.payload(), TripEndedPayload.class);

        PaymentCreateCommand command = new PaymentCreateCommand(
                payload.finalFare(),
                "CARD",
                payload.passengerId(),
                payload.notificationOriginId()
        );

        //이벤트 처리 로직
        paymentService.createPayment(command, payload.driverId());

        //처리 완료 후 커밋
        ack.acknowledge();

            log.info("TripEndedConsumer processed successfully. eventId={}, tripId={}",envelope.eventId(),payload.notificationOriginId());
        } catch (Exception e) {
            log.error("TripEndedConsumer processing failed. eventId={}, eventType={}",envelope.eventId(),envelope.eventType(),e);
            throw e;
            //예외 처리 로직 (재시도, 알림 등)
        }
    }

}
