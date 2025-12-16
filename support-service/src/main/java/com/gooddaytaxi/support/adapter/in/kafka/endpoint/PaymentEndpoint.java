package com.gooddaytaxi.support.adapter.in.kafka.endpoint;


import com.gooddaytaxi.support.adapter.in.kafka.dto.*;
import com.gooddaytaxi.support.application.Metadata;
import com.gooddaytaxi.support.application.dto.*;
import com.gooddaytaxi.support.application.port.in.payment.NotifyCompletedPaymentUsecase;
import com.gooddaytaxi.support.application.port.in.payment.NotifyRefundUsecase;
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
    private final NotifyRefundUsecase notifyRefundUsecase;

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


    /**
     * 환불 요청이 오면 관리자(MASTER_ADMIN)들에게 알림을 전송하는 이벤트 리스너
     */
    @KafkaListener(topics = "refund.request.created", groupId = "support-service")
    public void onRefundRequestCreated(EventRequest req) {
        // Metadata
        Metadata metadata = req.eventMetadata().to();
        // Payload
        RefundRequestCreatedEventPayload pl = req.convertPayload(RefundRequestCreatedEventPayload.class);
        log.debug("[Check] Refund Request Created EventRequest 데이터: paymentId={}, notifierId={}, occuredAt={}", pl.notificationOriginId(), pl.notifierId(), metadata.getOccuredAt());

        // EventRequest DTO > Command 변환
        NotifyRefundRequestedCommand command = NotifyRefundRequestedCommand.create(
                pl.notificationOriginId(), pl.notifierId(),
//                pl.dispatchId(),
                pl.tripId(),
                pl.paymentId(),
                pl.driverId(), pl.passengerId(),
                pl.amount(), pl.method(),
                pl.reason(), pl.requestedAt(),
                metadata
        );
        log.debug("[Transform] EventRequest >>> Command ➡️ {}", command);

        // 환불 요청 알림 전송 서비스 호출
        notifyRefundUsecase.request(command);
    }

    /**
     * 환불 요청이 거절되면 승객에게 알림을 전송하는 이벤트 리스너
     */
    @KafkaListener(topics = "refund.request.rejected", groupId = "support-service")
    public void onRefundRequestRejected(EventRequest req) {
        // Metadata
        Metadata metadata = req.eventMetadata().to();
        // Payload
        RefundRequestRejectedEventPayload pl = req.convertPayload(RefundRequestRejectedEventPayload.class);
        log.debug("[Check] Refund Request Rejected EventRequest 데이터: paymentId={}, notifierId={}, occuredAt={}", pl.notificationOriginId(), pl.notifierId(), metadata.getOccuredAt());

        // EventRequest DTO > Command 변환
        NotifyRefundRejectedCommand command = NotifyRefundRejectedCommand.create(
                pl.notificationOriginId(), pl.notifierId(),
//                pl.dispatchId(),
                pl.tripId(),
                pl.paymentId(),
                pl.passengerId(),
                pl.rejectReason(),
                pl.rejectedAt(),
                metadata
        );
        log.debug("[Transform] EventRequest >>> Command ➡️ {}", command);

        // 환불 요청 알림 전송 서비스 호출
        notifyRefundUsecase.reject(command);
    }

    /**
     * 환불이 완료되면 승객과 기사에게 알림을 전송하는 이벤트 리스너
     */
    @KafkaListener(topics = "refund.completed", groupId = "support-service")
    public void onRefundCompleted(EventRequest req) {
        // Metadata
        Metadata metadata = req.eventMetadata().to();
        // Payload
        RefundCompletedEventPayload pl = req.convertPayload(RefundCompletedEventPayload.class);
        log.debug("[Check] Refund Request Rejected EventRequest 데이터: paymentId={}, notifierId={}, occuredAt={}", pl.notificationOriginId(), pl.notifierId(), metadata.getOccuredAt());

        // EventRequest DTO > Command 변환
        NotifyRefundCompletedCommand command = NotifyRefundCompletedCommand.create(
                pl.notificationOriginId(), pl.notifierId(),
//                pl.dispatchId(),
                pl.tripId(),
                pl.paymentId(),
                pl.driverId(),
                pl.passengerId(),
                pl.method(),
                pl.amount(),
                pl.pgProvider(),
                pl.reason(),
                pl.message(),
                pl.approvedAt(),
                pl.refundedAt(),
                metadata
        );
        log.debug("[Transform] EventRequest >>> Command ➡️ {}", command);

        // 환불 요청 알림 전송 서비스 호출
        notifyRefundUsecase.complete(command);
    }


    /**
     * 기사가 직접 결제했을 경우, 환불 승인 시 기사에게 승객의 환불을 진행하라는 알림을 전송하는 이벤트 리스너
     */
    @KafkaListener(topics = "refund.settlement", groupId = "support-service")
    public void onRefundSettlementCreated(EventRequest req) {
        // Metadata
        Metadata metadata = req.eventMetadata().to();
        // Payload
        RefundSettlementCreatedEventPayload pl = req.convertPayload(RefundSettlementCreatedEventPayload.class);
        log.debug("[Check] Refund Request to Driver EventRequest 데이터: tripId={}, paymentId={}, driverId={}, approvedAt={}", pl.tripId(), pl.notificationOriginId(), pl.driverId(), pl.approvedAt());

        // EventRequest DTO > Command 변환
        NotifyRefundSettlementCreatedCommand command = NotifyRefundSettlementCreatedCommand.create(
                pl.notificationOriginId(), pl.notifierId(),
//                pl.dispatchId(),
                pl.tripId(),
                pl.paymentId(),
                pl.driverId(),
                pl.method(),
                pl.amount(),
                pl.reason(),
                pl.approvedAt(),
                metadata
        );
        log.debug("[Transform] EventRequest >>> Command ➡️ {}", command);

        // 환불 요청 알림 전송 서비스 호출
        notifyRefundUsecase.createSettlement(command);
    }

}
