package com.gooddaytaxi.support.adapter.in.kafka.endpoint;

import com.gooddaytaxi.support.adapter.in.kafka.dto.DispatchCallRequestReq;
import com.gooddaytaxi.support.application.dto.CreateCallCommand;
import com.gooddaytaxi.support.application.port.in.dispatch.RequestCallUsecase;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Dispatch 엔드포인트 - Dispatch로부터 발생하는 이벤트에 대한 리스너
 */
@Component
@RequiredArgsConstructor
public class DispatchEndpoint {

    private final RequestCallUsecase requestCallUsecase;

    @KafkaListener(topics = "dispatch.call-request", groupId = "support-service")
    public void onCallRequest(DispatchCallRequestReq req) {
//        DispatchCallRequestReq req = DispatchCallRequestReq.from(message);
        CreateCallCommand command = CreateCallCommand.create(
                req.notificationOriginId(), req.notifierId(),
                req.dispatchId(), req.driverId(), req.passengerId(),
                req.pickupAddress(), req.destinationAddress(),
                req.message());

        requestCallUsecase.request(command);
    }

//    @KafkaListener(topics = "dispatch.dispatch-accepted", groupId = "support-service")
//    public void onDispatchAccepted(String message) {
//        service.handleDispatchAcceptedEvent(message);
//    }
}
