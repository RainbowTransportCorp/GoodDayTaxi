package com.gooddaytaxi.support.adapter.in.kafka.endpoint;

import com.gooddaytaxi.support.adapter.in.kafka.dto.ErrorDetectedEventPayload;
import com.gooddaytaxi.support.adapter.in.kafka.dto.EventRequest;
import com.gooddaytaxi.support.application.dto.Metadata;
import com.gooddaytaxi.support.application.dto.input.log.ErrorDetectedCommand;
import com.gooddaytaxi.support.application.port.in.monitoring.NotifyErrorDetectUsecase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Error, Log 이벤트 리스너
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LogEndpoint {

    private final NotifyErrorDetectUsecase notifyErrorDetectUsecase;

    /**
     * 각 도메인에서 발생하는, 시스템에서 발생하는 에러 알림 전송 이벤트 리스너
     */
    @KafkaListener(topics = "dispatch.requested", groupId = "support-service", concurrency = "1")
    public void onErrorDetected(EventRequest req) {
        log.info("💗💗💗💗 Requested Request: {}", req.toString());
        // Metadata
        Metadata metadata = new Metadata(req.eventId(), req.eventType(), req.occurredAt());
        // Payload
        ErrorDetectedEventPayload pl = req.convertPayload(ErrorDetectedEventPayload.class);
        log.debug("[Check] Error Detect EventRequest 데이터: notificationOriginId={}, notifierId={}, logType={}, occurredAt={}", pl.notificationOriginId(), pl.notifierId(), pl.logType(), metadata.occurredAt());

        // EventRequest DTO > Command 변환
        ErrorDetectedCommand command = ErrorDetectedCommand.create(
                pl.notificationOriginId(), pl.notifierId(),
                pl.dispatchId(), pl.tripId(), pl.paymentId(),
                pl.driverId(), pl.passengerId(),
                pl.sourceNotificationType(), pl.logType(),
                pl.logMessage(),
                metadata
        );

        log.debug("[Transform] EventRequest >>> Command ➡️ {}", command);

        // 탐지된 에러에 대해 알림 전송 서비스 호출
        notifyErrorDetectUsecase.execute(command);

    }
}
