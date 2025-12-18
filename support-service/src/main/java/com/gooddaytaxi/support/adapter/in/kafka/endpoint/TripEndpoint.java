package com.gooddaytaxi.support.adapter.in.kafka.endpoint;

import com.gooddaytaxi.support.adapter.in.kafka.dto.EventRequest;
import com.gooddaytaxi.support.adapter.in.kafka.dto.TripCanceledEventPayload;
import com.gooddaytaxi.support.adapter.in.kafka.dto.TripEndedEventPayload;
import com.gooddaytaxi.support.adapter.in.kafka.dto.TripStartedEventPayload;
import com.gooddaytaxi.support.application.dto.Metadata;
import com.gooddaytaxi.support.application.dto.trip.NotifyTripCanceledCommand;
import com.gooddaytaxi.support.application.dto.trip.NotifyTripEndedCommand;
import com.gooddaytaxi.support.application.dto.trip.NotifyTripStartedCommand;
import com.gooddaytaxi.support.application.port.in.trip.NotifyCanceledTripUsecase;
import com.gooddaytaxi.support.application.port.in.trip.NotifyEndedTripUsecase;
import com.gooddaytaxi.support.application.port.in.trip.NotifyStartedTripUsecase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Trip 엔드포인트 - Trip으로부터 발생하는 이벤트에 대한 리스너
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TripEndpoint {

    private final NotifyStartedTripUsecase notifyStartedTripUsecase;
    private final NotifyEndedTripUsecase notifyEndedTripUsecase;
    private final NotifyCanceledTripUsecase notifyCanceledTripUsecase;

    /**
     * 운행이 시작될 때 기사, 손님에게 알림을 전송하는 이벤트 리스너
     */
    @KafkaListener(topics = "trip.started", groupId = "support-service")
    public void onTripStarted(EventRequest req) {
        // Metadata
        Metadata metadata = new Metadata(req.eventId(), req.eventType(), req.occurredAt());
        // Payload
        TripStartedEventPayload pl = req.convertPayload(TripStartedEventPayload.class);
        log.debug("[Check] Trip Started EventRequest 데이터: tripId={}, notifierId={}, occurredAt={}", pl.notificationOriginId(), pl.notifierId(), metadata.occurredAt());

        // EventRequest DTO > Command 변환
        NotifyTripStartedCommand command = NotifyTripStartedCommand.create(
                pl.notificationOriginId(), pl.notifierId(),
                pl.dispatchId(),
                pl.driverId(), pl.passengerId(),
                pl.pickupAddress(), pl.destinationAddress(),
                pl.startTime(),
                metadata
        );
        log.debug("[Transform] EventRequest >>> Command ➡️ {}", command);

        // 운행 시작 알림 전송 서비스 호출
        notifyStartedTripUsecase.execute(command);
    }

    /**
     * 운행이 종료될 때 기사, 손님에게 알림을 전송하는 이벤트 리스너
     */
    @KafkaListener(topics = "trip.ended", groupId = "support-service")
    public void onTripEnded(EventRequest req) {
        // Metadata
        Metadata metadata = new Metadata(req.eventId(), req.eventType(), req.occurredAt());
        // Payload
        TripEndedEventPayload pl = req.convertPayload(TripEndedEventPayload.class);
        log.debug("[Check] Trip Ended EventRequest 데이터: tripId={}, notifierId={}, occurredAt={}", pl.notificationOriginId(), pl.notifierId(), metadata.occurredAt());

        // EventRequest DTO > Command 변환
        NotifyTripEndedCommand command = NotifyTripEndedCommand.create(
                pl.notificationOriginId(), pl.notifierId(),
                pl.dispatchId(),
                pl.driverId(), pl.passengerId(),
                pl.pickupAddress(), pl.destinationAddress(),
                pl.startTime(), pl.endTime(),
                pl.totalDuration(), pl.totalDistance(),
                pl.finalFare(),
                metadata
        );
        log.debug("[Transform] EventRequest >>> Command ➡️ {}", command);

        // 운행 종료 알림 전송 서비스 호출
        notifyEndedTripUsecase.execute(command);
    }

    /**
     * 운행이 종료될 때 기사, 손님에게 알림을 전송하는 이벤트 리스너
     */
    @KafkaListener(topics = "trip.canceled", groupId = "support-service")
    public void onTripCanceled(EventRequest req) {
        // Metadata
        Metadata metadata = new Metadata(req.eventId(), req.eventType(), req.occurredAt());
        // Payload
        TripCanceledEventPayload pl = req.convertPayload(TripCanceledEventPayload.class);
        log.debug("[Check] Trip Canceled EventRequest 데이터: tripId={}, driverId={}, cancelReason={}, canceledAt={}", pl.notificationOriginId(), pl.driverId(), pl.cancelReason(), pl.canceledAt());

        // EventRequest DTO > Command 변환
        NotifyTripCanceledCommand command = NotifyTripCanceledCommand.create(
                pl.notificationOriginId(), pl.notifierId(),
                pl.dispatchId(),
                pl.driverId(), pl.passengerId(),
                pl.cancelReason(),
                pl.canceledAt(),
                metadata
        );
        log.debug("[Transform] EventRequest >>> Command ➡️ {}", command);

        // 운행 취소 알림 전송 서비스 호출
        notifyCanceledTripUsecase.execute(command);
    }

}
