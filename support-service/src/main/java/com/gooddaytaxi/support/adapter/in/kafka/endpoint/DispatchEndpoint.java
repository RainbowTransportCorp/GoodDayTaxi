package com.gooddaytaxi.support.adapter.in.kafka.endpoint;

import com.gooddaytaxi.support.adapter.in.kafka.dto.DispatchRequestReq;
import com.gooddaytaxi.support.adapter.in.kafka.dto.Payload;
import com.gooddaytaxi.support.application.dto.CreateDispatchInfoCommand;
import com.gooddaytaxi.support.application.port.in.dispatch.AcceptDispatchUsecase;
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
    private final AcceptDispatchUsecase acceptDispatchUsecase;

    /** 특정 기사에게 배차 요청이 왔을 때 Driver에 손님의 Call 요청 알림 전송 이벤트 리스너
     */
    @KafkaListener(topics = "dispatch.requested", groupId = "support-service", concurrency = "1")
    public void onDispatchRequested(DispatchRequestReq req) {

        Payload p = req.payload();
        log.info("‼️‼️‼️‼️Payload 내용 content={}", p);
        log.info("‼️‼️‼️‼️Event 수신 내용 eventId={}, evnetType{}, occurredAt={}, payloadVersion={}",
        req.eventId(),
        req.eventType(),
        req.occurredAt(),
        req.payloadVersion());

        log.info("‼️‼️‼️‼️Request 내용 message={}, driverId={}, passengerId={}",
                p.message(), p.driverId(), p.passengerId());


//        DispatchRequestReq req = DispatchRequestReq.from(message);
        CreateDispatchInfoCommand command = CreateDispatchInfoCommand.create(
                p.notificationOriginId(), p.notifierId(),
                p.driverId(), p.passengerId(),
                p.pickupAddress(), p.destinationAddress(),
                p.message());

        log.info("‼️‼️‼️‼️Command 내용 message={}, driverId={}, passengerId={}",
                command.getMessage(), command.getDriverId(), command.getPassengerId());


        notifyDispatchUsecase.request(command);
    }

    /** 기사가 콜을 수락(배차 완료)했을 때 알림 전송 이벤트 리스너
     */
//    @KafkaListener(topics = "dispatch.accepted", groupId = "support-service")
//    public void onDispatchAccepted(DispatchRequestReq req) {
//        GetDispatchInfoCommand command = GetDispatchInfoCommand.create(
//                req.notificationOriginId(), req.notifierId(),
//                req.driverId(), req.passengerId(),
//                req.pickupAddress(), req.destinationAddress(),
//                req.message());
////        acceptDispatchUsecase.accepted(message);
//    }
}
