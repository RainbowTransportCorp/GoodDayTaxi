package com.gooddaytaxi.support.application.dto.payment;

import com.gooddaytaxi.support.application.Metadata;
import com.gooddaytaxi.support.application.dto.Command;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 환불 요청 알림 Command
 * - REFUND_REQUEST_CREATED 이벤트 처리
 */
@Getter
public class NotifyRefundRequestedCommand extends Command {
    private final UUID refundRequestId;
    //    private final UUID dispatchId;
    private final UUID tripId;
    private final UUID paymentId;
    private final UUID driverId;
    private final UUID passengerId;
    private final Long amount;
    private final String paymentMethod;
    private final String reason;
    private final LocalDateTime requestedAt;

    private NotifyRefundRequestedCommand(
            UUID notificationOriginId, UUID notifierId,
//            UUID dispatchId,
            UUID tripId,
            UUID paymentId,
            UUID driverId, UUID passengerId,
            Long amount, String paymentMethod,
            String reason,
            LocalDateTime requestedAt,
            Metadata metadata
    ) {
        super(notificationOriginId, notifierId, null, metadata);
        this.refundRequestId = notificationOriginId;
//        this.dispatchId = dispatchId;
        this.tripId = tripId;
        this.paymentId = paymentId;
        this.driverId = driverId;
        this.passengerId = passengerId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.reason = reason;
        this.requestedAt = requestedAt;
    }
    public static NotifyRefundRequestedCommand create(
            UUID notificationOriginId, UUID notifierId,
//            UUID dispatchId,
            UUID tripId,
            UUID paymentId,
            UUID driverId, UUID passengerId,
            Long amount, String paymentMethod,
            String reason,
            LocalDateTime requestedAt,
            Metadata metadata
    ) {
        return new NotifyRefundRequestedCommand(notificationOriginId, notifierId, tripId, paymentId, driverId, passengerId, amount, paymentMethod, reason, requestedAt, metadata);
    }
}
