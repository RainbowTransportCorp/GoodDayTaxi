package com.gooddaytaxi.support.adapter.in.kafka.endpoint;


import com.gooddaytaxi.support.adapter.in.kafka.dto.EventRequest;
import com.gooddaytaxi.support.adapter.in.kafka.dto.PaymentCompletedEventPayload;
import com.gooddaytaxi.support.adapter.in.kafka.dto.TripStartedEventPayload;
import com.gooddaytaxi.support.application.Metadata;
import com.gooddaytaxi.support.application.dto.NotifyPaymentCompletedCommand;
import com.gooddaytaxi.support.application.dto.NotifyTripStartedCommand;
import com.gooddaytaxi.support.application.port.in.payment.NotifyCompletedPaymentUsecase;
import com.gooddaytaxi.support.application.port.in.trip.NotifyStartedTripUsecase;
import com.gooddaytaxi.support.application.service.PaymentNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Payment 엔드포인트 - Payment로부터 발생하는 이벤트에 대한 리스너
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEndpoint {

    private final NotifyCompletedPaymentUsecase notifyCompletedPaymentUsecase;

    /**
     * 결제가 완료되면, 기사, 손님에게 알림을 전송하는 이벤트 리스너
     */
    @KafkaListener(topics = "payment.completed", groupId = "support-service")
    public void onPaymentCompleted(EventRequest req) {
        // Metadata
        Metadata metadata = req.eventMetadata().to();
        // Payload
        PaymentCompletedEventPayload pl = req.convertPayload(PaymentCompletedEventPayload.class);
        log.debug("[Check] Payment Completed EventRequest 데이터: paymentId={}, notifierId={}, occuredAt={}", pl.notificationOriginId(), pl.notifierId(), metadata.getOccuredAt());

        // EventRequest DTO > Command 변환
        NotifyPaymentCompletedCommand command = NotifyPaymentCompletedCommand.create(
                pl.notificationOriginId(), pl.notifierId(),
//                pl.dispatchId(),
                pl.tripId(),
                pl.driverId(), pl.passengerId(),
                pl.amount(), pl.method(),
                pl.approvedAt(),
                metadata
        );
        log.debug("[Transform] EventRequest >>> Command ➡️ {}", command);

        // 결제 완료 알림 전송 서비스 호출
        notifyCompletedPaymentUsecase.execute(command);
    }
}
