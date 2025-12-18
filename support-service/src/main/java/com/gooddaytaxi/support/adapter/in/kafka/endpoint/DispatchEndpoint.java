package com.gooddaytaxi.support.adapter.in.kafka.endpoint;

import com.gooddaytaxi.support.adapter.in.kafka.dto.*;
import com.gooddaytaxi.support.application.dto.Metadata;
import com.gooddaytaxi.support.application.dto.input.dispatch.*;
import com.gooddaytaxi.support.application.port.in.dispatch.*;
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
    private final NotifyCallAcceptUsecase notifyCallAcceptUsecase;
    private final NotifyDispatchTimeoutUsecase notifyDispatchTimeoutUsecase;
    private final NotifyDispatchCancelUsecase notifyDispatchCancelUsecase;
    private final NotifyDispatchRejectUsecase notifyDispatchRejectUsecase;

    /**
     * 특정 기사에게 배차 요청이 왔을 때 기사에게 손님의 Call 요청 알림 전송 이벤트 리스너
     */
    @KafkaListener(topics = "dispatch.requested", groupId = "support-service", concurrency = "1")
    public void onDispatchRequested(EventRequest req) {
        log.info("💗💗💗💗 Requested Request: {}", req.toString());
        // Metadata
        Metadata metadata = new Metadata(req.eventId(), req.eventType(), req.occurredAt());
        // Payload
        DispatchRequestedEventPayload pl = req.convertPayload(DispatchRequestedEventPayload.class);
        log.debug("[Check] Dispatch EventRequest 데이터: dispatchId={}, notifierId={}, message={}, occurredAt={}", pl.notificationOriginId(), pl.notifierId(), pl.message(), metadata.occurredAt());

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
        log.info("💗💗💗💗 Accepted Request: {}", req.toString());
        // Metadata
        Metadata metadata = new Metadata(req.eventId(), req.eventType(), req.occurredAt());
        // Payload
        DispatchAcceptedEventPayload pl = req.convertPayload(DispatchAcceptedEventPayload.class);
        log.info("[Check] Dispatch EventRequest 데이터: dispatchId={}, notifierId={}, message={}, occurredAt={}", pl.notificationOriginId(), pl.notifierId(), pl.message(), metadata.occurredAt());

        // EventRequest DTO > Command 변환
        NotifyDispatchAcceptedCommand command = NotifyDispatchAcceptedCommand.create(
                pl.notificationOriginId(), pl.notifierId(),
                pl.driverId(), pl.passengerId(),
                pl.pickupAddress(), pl.destinationAddress(),
                pl.message(),
                pl.acceptedAt(),
                metadata
        );
        log.debug("[Transform] EventRequest >>> Command ➡️ {}", command);

        // 수락된 콜 알림 전송 서비스 호출
        notifyCallAcceptUsecase.execute(command);
    }


    /**
     * 기사가 배차 요청에 대해 응답하지 않은 지 30초가 경과되면 손님에게 배차 시간 초과(Call 거절) 알림을 전송하는 이벤트 리스너
     */
    @KafkaListener(topics = "dispatch.timeout", groupId = "support-service", concurrency = "1")
    public void onDispatchTimeOut(EventRequest req) {
        log.info("💗💗💗💗 Timeout Request: {}", req.toString());

        // Metadata
        Metadata metadata = new Metadata(req.eventId(), req.eventType(), req.occurredAt());
        // Payload
        DispatchTimeoutEventPayload pl = req.convertPayload(DispatchTimeoutEventPayload.class);
        log.debug("[Check] Dispatch Timeout EventRequest 데이터: dispatchId={}, timeoutAt={}", pl.dispatchId(), pl.timeoutAt());

        // EventRequest DTO > Command 변환
        NotifyDipsatchTimeoutCommand command = NotifyDipsatchTimeoutCommand.create(
                pl.notificationOriginId(),
                pl.notifierId(),
                pl.passengerId(),
                pl.message(),
                pl.timeoutAt(),
                metadata
        );
        log.debug("[Transform] EventRequest >>> Command ➡️ {}", command);

        // 수락된 콜 알림 전송 서비스 호출
        notifyDispatchTimeoutUsecase.execute(command);
    }


    /**
     * 기사의 콜 수락 후, 승객이 콜을 취소했을 때 기사에게 알림을 전송하는 이벤트 리스너
     */
    @KafkaListener(topics = "dispatch.canceled", groupId = "support-service", concurrency = "1")
    public void onDispatchCancelled(EventRequest req) {

        log.info("💗💗💗💗 Canceled Request: {}", req.toString());
        // Metadata
        Metadata metadata = new Metadata(req.eventId(), req.eventType(), req.occurredAt());
        // Payload
        DispatchCanceledEventPayload pl = req.convertPayload(DispatchCanceledEventPayload.class);
        log.debug("[Check] Dispatch Cancel EventRequest 데이터: dispatchId={}, driverId={}, cancelBy={}, canceledAt={}", pl.notificationOriginId(), pl.driverId(), pl.canceledBy(), pl.canceledAt());

        // EventRequest DTO > Command 변환
        NotifyDispatchCanceledCommand command = NotifyDispatchCanceledCommand.create(
                pl.notificationOriginId(),
                pl.notifierId(),
                pl.driverId(),
                pl.passengerId(),
                pl.message(),
                pl.canceledBy(),
                pl.canceledAt(),
                metadata
        );
        log.debug("[Transform] EventRequest >>> Command ➡️ {}", command);

        // 배차 취소 알림 전송 서비스 호출
        notifyDispatchCancelUsecase.execute(command);
    }

    /**
     * 어느 기사가 콜을 거절했을 때 이미 배정된 콜에 대해 같은 콜을 받은 다른 기사들에게 거절 알림을 전송하는 이벤트 리스너
     */
    @KafkaListener(topics = "dispatch.rejected", groupId = "support-service", concurrency = "1")
    public void onDispatchRejected(EventRequest req) {
        log.info("💗💗💗💗 Rejected Request: {}", req.toString());
        // Metadata
        Metadata metadata = new Metadata(req.eventId(), req.eventType(), req.occurredAt());
        // Payload
        DispatchRejectedEventPayload pl = req.convertPayload(DispatchRejectedEventPayload.class);
        log.debug("[Check] Dispatch Reject EventRequest 데이터: dispatchId={}, driverId={}, rejectedAt={}", pl.dispatchId(), pl.driverId(), pl.rejectedAt());

        // EventRequest DTO > Command 변환
        NotifyDispatchRejectedCommand command = NotifyDispatchRejectedCommand.create(
                pl.notificationOriginId(),
                pl.notifierId(),
                pl.driverId(),
                pl.passengerId(),
                pl.message(),
                pl.rejectedAt(),
                metadata
        );
        log.debug("[Transform] EventRequest >>> Command ➡️ {}", command);

        // 배차 취소 알림 전송 서비스 호출
        notifyDispatchRejectUsecase.execute(command);
    }
}