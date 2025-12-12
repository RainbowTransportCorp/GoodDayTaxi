package com.gooddaytaxi.support.adapter.in.kafka.endpoint;

import com.gooddaytaxi.support.adapter.in.kafka.dto.DispatchRequest;
import com.gooddaytaxi.support.adapter.in.kafka.dto.Payload;
import com.gooddaytaxi.support.application.Metadata;
import com.gooddaytaxi.support.application.dto.NotifyDispatchAcceptedCommand;
import com.gooddaytaxi.support.application.dto.NotifyDispatchInformationCommand;
import com.gooddaytaxi.support.application.port.in.dispatch.NotifyAcceptedCallUsecase;
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

    /**
     * 특정 기사에게 배차 요청이 왔을 때 Driver에 손님의 Call 요청 알림 전송 이벤트 리스너
     */
    @KafkaListener(topics = "dispatch.requested", groupId = "support-service", concurrency = "1")
    public void onDispatchRequested(DispatchRequest req) {

        Payload p = req.payload();
        log.info("‼️‼️‼️‼️Payload 내용 content={}", p);
        log.info("‼️‼️‼️‼️Event 수신 내용 eventId={}, evnetType{}, occurredAt={}, payloadVersion={}",
        req.metadata().getEventId(),
        req.metadata().getEventType(),
        req.metadata().getOccuredAt(),
        req.payloadVersion());

        log.info("‼️‼️‼️‼️Request 내용 message={}, driverId={}, passengerId={}",
                p.message(), p.driverId(), p.passengerId());


//        DispatchRequest req = DispatchRequest.from(message);
        NotifyDispatchInformationCommand command = NotifyDispatchInformationCommand.create(
                p.notificationOriginId(), p.notifierId(),
                p.driverId(), p.passengerId(),
                p.pickupAddress(), p.destinationAddress(),
                p.message());

        log.info("‼️‼️‼️‼️Command 내용 message={}, driverId={}, passengerId={}",
                command.getMessage(), command.getDriverId(), command.getPassengerId());


        notifyDispatchUsecase.execute(command);
    }

    /**
     * 기사가 배차 요청 수락 후, 손님에게 Call 수락 알림을 전송하는 이벤트 리스너
     */
    @KafkaListener(topics = "dispatch.accepted", groupId = "support-service", concurrency = "1")
    public void onDispatchAccepted(DispatchRequest req) {
        // Payload 추출
        Payload pl = req.payload();
        Metadata metadata = req.metadata();
        log.debug("[Check] Dispatch Request 데이터: dispatchId={}, notifierId={}, message={}, occuredAt={}", pl.notificationOriginId(), pl.notifierId(), pl.message(), metadata.getOccuredAt());

        // Request DTO > Command 변환
        NotifyDispatchAcceptedCommand command = NotifyDispatchAcceptedCommand.create(
                pl.notificationOriginId(), pl.notifierId(),
                pl.driverId(), pl.passengerId(),
                pl.pickupAddress(), pl.destinationAddress(),
                pl.message(),
                metadata
        );
        log.debug("[Transform] EventMetadata >>> Command ➡️ {}", command);

        // 수락된 콜 알림 전송 서비스 호출
        notifyAcceptedCallUsecase.execute(command);
    }
}