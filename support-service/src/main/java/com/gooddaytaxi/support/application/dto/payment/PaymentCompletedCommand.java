package com.gooddaytaxi.support.application.dto.payment;

import com.gooddaytaxi.support.application.dto.Metadata;
import com.gooddaytaxi.support.application.dto.Command;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 결제 완료 알림 Command
 * - PAYMENT_COMPLETED 이벤트 처리
 */
@Getter
public class PaymentCompletedCommand extends Command {
    //    private final UUID dispatchId;
    private final UUID tripId;
    private final UUID paymentId;
    private final UUID driverId;
    private final UUID passengerId;
    private final Long amount;
    private final String paymentMethod;
    private final LocalDateTime approvedAt;

    private PaymentCompletedCommand(
            UUID notificationOriginId, UUID notifierId,
//            UUID dispatchId,
            UUID tripId,
            UUID driverId, UUID passengerId,
            Long amount, String paymentMethod,
            LocalDateTime approvedAt,
            Metadata metadata
    ) {
        super(notificationOriginId, notifierId, null, metadata);
        this.paymentId = notificationOriginId;
//        this.dispatchId = dispatchId;
        this.tripId = tripId;
        this.driverId = driverId;
        this.passengerId = passengerId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.approvedAt = approvedAt;

    }
    public static PaymentCompletedCommand create(
            UUID notificationOriginId, UUID notifierId,
//            UUID dispatchId,
            UUID tripId,
            UUID driverId, UUID passengerId,
            Long amount, String paymentMethod,
            LocalDateTime approvedAt,
            Metadata metadata
    ) {
        return new PaymentCompletedCommand(notificationOriginId, notifierId, tripId, driverId, passengerId, amount, paymentMethod, approvedAt, metadata);
    }
}