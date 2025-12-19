package com.gooddaytaxi.support.application.dto.input.payment;

import com.gooddaytaxi.support.application.dto.Metadata;
import com.gooddaytaxi.support.application.dto.input.Command;
import com.gooddaytaxi.support.domain.notification.model.Notification;
import com.gooddaytaxi.support.domain.notification.model.NotificationType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class RefundCompletedCommand extends Command {
    private final UUID refundRequestId;
    private final UUID adminId;
    //    private final UUID dispatchId;
    private final UUID tripId;
    private final UUID paymentId;
    private final UUID driverId;
    private final UUID passengerId;
    private final String method;
    private final Long amount;
    private final String pgProvider;
    private final String reason;
    private final LocalDateTime approvedAt;
    private final LocalDateTime refundedAt;

    private RefundCompletedCommand(
            UUID notificationOriginId, UUID notifierId,
//            UUID dispatchId,
            UUID tripId,
            UUID paymentId,
            UUID driverId,
            UUID passengerId,
            String method,
            Long amount,
            String pgProvider,
            String reason,
            String message,
            LocalDateTime approvedAt,
            LocalDateTime refundedAt,
            Metadata metadata
    ) {
        super(notificationOriginId, notifierId, message, metadata);
        this.refundRequestId = notificationOriginId;
        this.adminId = notifierId;
//        this.dispatchId = dispatchId;
        this.tripId = tripId;
        this.paymentId = paymentId;
        this.driverId = driverId;
        this.passengerId = passengerId;
        this.method = method;
        this.amount = amount;
        this.pgProvider = pgProvider;
        this.reason = reason;
        this.approvedAt = approvedAt;
        this.refundedAt = refundedAt;
    }
    public static RefundCompletedCommand create(
            UUID notificationOriginId, UUID notifierId,
//            UUID dispatchId,
            UUID tripId,
            UUID paymentId,
            UUID driverId,
            UUID passengerId,
            String method,
            Long amount,
            String pgProvider,
            String reason,
            String message,
            LocalDateTime approvedAt,
            LocalDateTime refundedAt,
            Metadata metadata
    ) {
        return new RefundCompletedCommand(notificationOriginId, notifierId, tripId, paymentId, driverId, passengerId, method, amount, pgProvider, reason, message, approvedAt, refundedAt, metadata);
    }

    public Notification toEntity(NotificationType notificationType) {
        return Notification.create(this.getNotificationOriginId(), this.getNotifierId(), notificationType, this.getMessage());
    }
}


