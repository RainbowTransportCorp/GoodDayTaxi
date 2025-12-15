package com.gooddaytaxi.support.adapter.in.kafka.endpoint;

import com.gooddaytaxi.support.adapter.in.kafka.dto.DispatchCancelledEventPayload;
import com.gooddaytaxi.support.adapter.in.kafka.dto.DispatchEventPayload;
import com.gooddaytaxi.support.adapter.in.kafka.dto.DispatchTimeoutEventPayload;
import com.gooddaytaxi.support.adapter.in.kafka.dto.EventRequest;
import com.gooddaytaxi.support.application.Metadata;
import com.gooddaytaxi.support.application.dto.NotifyDipsatchTimeoutCommand;
import com.gooddaytaxi.support.application.dto.NotifyDispatchAcceptedCommand;
import com.gooddaytaxi.support.application.dto.NotifyDispatchCancelledCommand;
import com.gooddaytaxi.support.application.dto.NotifyDispatchInformationCommand;
import com.gooddaytaxi.support.application.port.in.dispatch.NotifyAcceptedCallUsecase;
import com.gooddaytaxi.support.application.port.in.dispatch.NotifyDispatchCancelUsecase;
import com.gooddaytaxi.support.application.port.in.dispatch.NotifyDispatchTimeoutUsecase;
import com.gooddaytaxi.support.application.port.in.dispatch.NotifyDispatchUsecase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Dispatch 엔드포인트 - Dispatch로부터 발생하는 이벤트에 대한 리스너
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DispatchEndpoint {

    private final NotifyDispatchUsecase notifyDispatchUsecase;
    private final NotifyAcceptedCallUsecase notifyAcceptedCallUsecase;
    private final NotifyDispatchTimeoutUsecase notifyDispatchTimeoutUsecase;
    private final NotifyDispatchCancelUsecase notifyDispatchCancelUsecase;

    /**
     * 특정 기사에게 배차 요청이 왔을 때 Driver에 손님의 Call 요청 알림 전송 이벤트 리스너
     */
    @KafkaListener(topics = "dispatch.requested", groupId = "support-service", concurrency = "1")
    public void onDispatchRequested(EventRequest req) {
        // Metadata
        Metadata metadata = req.eventMetadata().to();
        // Payload
        DispatchEventPayload pl = req.convertPayload(DispatchEventPayload.class);
        log.debug("[Check] Dispatch EventRequest 데이터: dispatchId={}, notifierId={}, message={}, occuredAt={}", pl.notificationOriginId(), pl.notifierId(), pl.message(), metadata.getOccuredAt());

        // EventRequest DTO > Command 변환
        NotifyDispatchInformationCommand command = NotifyDispatchInformationCommand.create(
                pl.notificationOriginId(), pl.notifierId(),
                pl.driverId(), pl.passengerId(),
                pl.pickupAddress(), pl.destinationAddress(),
                pl.message(),
                metadata
        );

        log.debug("[Transform] EventRequest >>> Command ➡️ {}", command);

        // 콜에 대해 배차 시도 알림 전송 서비스 호출
        notifyDispatchUsecase.execute(command);

    }

    /**
     * 기사가 배차 요청 수락 후, 손님에게 Call 수락 알림을 전송하는 이벤트 리스너
     */
    @KafkaListener(topics = "dispatch.accepted", groupId = "support-service", concurrency = "1")
    public void onDispatchAccepted(EventRequest req) {
        // Metadata
        Metadata metadata = req.eventMetadata().to();
        // Payload
        DispatchEventPayload pl = req.convertPayload(DispatchEventPayload.class);
        log.debug("[Check] Dispatch EventRequest 데이터: dispatchId={}, notifierId={}, message={}, occuredAt={}", pl.notificationOriginId(), pl.notifierId(), pl.message(), metadata.getOccuredAt());

        // EventRequest DTO > Command 변환
        NotifyDispatchAcceptedCommand command = NotifyDispatchAcceptedCommand.create(
                pl.notificationOriginId(), pl.notifierId(),
                pl.driverId(), pl.passengerId(),
                pl.pickupAddress(), pl.destinationAddress(),
                pl.message(),
                metadata
        );
        log.debug("[Transform] EventRequest >>> Command ➡️ {}", command);

        // 수락된 콜 알림 전송 서비스 호출
        notifyAcceptedCallUsecase.execute(command);
    }


    /**
     * 기사가 배차 요청에 대해 응답하지 않은 지 30초가 경과되면 손님에게 배차 시간 초과(Call 거절) 알림을 전송하는 이벤트 리스너
     */
    @KafkaListener(topics = "dispatch.timeout", groupId = "support-service", concurrency = "1")
    public void onDispatchTimeOut(EventRequest req) {
        // Metadata
        Metadata metadata = req.eventMetadata().to();
        // Payload
        DispatchTimeoutEventPayload pl = req.convertPayload(DispatchTimeoutEventPayload.class);
        log.debug("[Check] Dispatch Timeout EventRequest 데이터: dispatchId={}, timeoutAt={}", pl.dispatchId(), pl.timeoutAt());

        // EventRequest DTO > Command 변환
        NotifyDipsatchTimeoutCommand command = NotifyDipsatchTimeoutCommand.create(
                pl.dispatchId(),
                pl.passengerId(),
                pl.timeoutAt(),
                metadata
        );
        log.debug("[Transform] EventRequest >>> Command ➡️ {}", command);

        // 수락된 콜 알림 전송 서비스 호출
        notifyDispatchTimeoutUsecase.execute(command);
    }


    /**
     * 승객이 콜을 취소했을 때 기사에게 알림을 전송하는 이벤트 리스너
     */
    @KafkaListener(topics = "dispatch.cancelled", groupId = "support-service", concurrency = "1")
    public void onDispatchCancelled(EventRequest req) {
        // Metadata
        Metadata metadata = req.eventMetadata().to();
        // Payload
        DispatchCancelledEventPayload pl = req.convertPayload(DispatchCancelledEventPayload.class);
        log.debug("[Check] Dispatch Cancel EventRequest 데이터: dispatchId={}, driverId={}, cancelBy={}, cancelledAt={}", pl.dispatchId(), pl.driverId(), pl.cancelledBy(), pl.cancelledAt());

        // EventRequest DTO > Command 변환
        NotifyDispatchCancelledCommand command = NotifyDispatchCancelledCommand.create(
                pl.dispatchId(),
                pl.driverId(),
                pl.passengerId(),
                pl.cancelledBy(),
                pl.cancelledAt(),
                metadata
        );
        log.debug("[Transform] EventRequest >>> Command ➡️ {}", command);

        // 배차 취소 알림 전송 서비스 호출
        notifyDispatchCancelUsecase.execute(command);
    }
}