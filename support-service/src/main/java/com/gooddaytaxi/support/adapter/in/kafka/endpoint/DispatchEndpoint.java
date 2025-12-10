package com.gooddaytaxi.support.adapter.in.kafka.endpoint;

import com.gooddaytaxi.support.adapter.in.kafka.dto.DispatchRequestReq;
import com.gooddaytaxi.support.application.dto.CreateDispatchInfoCommand;
import com.gooddaytaxi.support.application.dto.GetDispatchInfoCommand;
import com.gooddaytaxi.support.application.port.in.dispatch.AcceptDispatchUsecase;
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
    private final AcceptDispatchUsecase acceptDispatchUsecase;

    /** 특정 기사에게 배차 요청이 왔을 때 Driver에 손님의 Call 요청 알림 전송 이벤트 리스너
     */
    @KafkaListener(topics = "dispatch.requested", groupId = "support-service")
    public void onDispatchRequested(DispatchRequestReq req) {
//        DispatchRequestReq req = DispatchRequestReq.from(message);
        CreateDispatchInfoCommand command = CreateDispatchInfoCommand.create(
                req.notificationOriginId(), req.notifierId(),
                req.driverId(), req.passengerId(),
                req.pickupAddress(), req.destinationAddress(),
                req.message());

        notifyDispatchUsecase.request(command);
    }

    /** 기사가 콜을 수락(배차 완료)했을 때 알림 전송 이벤트 리스너
     */
    @KafkaListener(topics = "dispatch.accepted", groupId = "support-service")
    public void onDispatchAccepted(DispatchRequestReq req) {
        GetDispatchInfoCommand command = GetDispatchInfoCommand.create(
                req.notificationOriginId(), req.notifierId(),
                req.driverId(), req.passengerId(),
                req.pickupAddress(), req.destinationAddress(),
                req.message());
//        acceptDispatchUsecase.accepted(message);
    }
}
