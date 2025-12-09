package com.gooddaytaxi.support.adapter.in.kafka.endpoint;

import com.gooddaytaxi.support.adapter.in.kafka.dto.DispatchRequestReq;
import com.gooddaytaxi.support.application.dto.CreateDispatchInfoCommand;
import com.gooddaytaxi.support.application.port.in.dispatch.NotifyDispatchUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Dispatch 엔드포인트 - Dispatch로부터 발생하는 이벤트에 대한 리스너
 */
@Component
@RequiredArgsConstructor
public class DispatchEndpoint {

    private final NotifyDispatchUsecase notifyDispatchUsecase;

    @KafkaListener(topics = "dispatch.requested", groupId = "support-service")
    public void onCallRequest(DispatchRequestReq req) {
//        DispatchRequestReq req = DispatchRequestReq.from(message);
        CreateDispatchInfoCommand command = CreateDispatchInfoCommand.create(
                req.notificationOriginId(), req.notifierId(),
                req.driverId(), req.passengerId(),
                req.pickupAddress(), req.destinationAddress(),
                req.message());

        notifyDispatchUsecase.request(command);
    }

//    @KafkaListener(topics = "dispatch.dispatch-accepted", groupId = "support-service")
//    public void onDispatchAccepted(String message) {
//        service.handleDispatchAcceptedEvent(message);
//    }
}
