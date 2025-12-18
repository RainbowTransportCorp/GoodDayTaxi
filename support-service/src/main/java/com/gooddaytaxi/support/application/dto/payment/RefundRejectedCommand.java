package com.gooddaytaxi.support.application.dto.payment;

import com.gooddaytaxi.support.application.dto.Metadata;
import com.gooddaytaxi.support.application.dto.Command;
import com.gooddaytaxi.support.domain.notification.model.Notification;
import com.gooddaytaxi.support.domain.notification.model.NotificationType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class RefundRejectedCommand extends Command {
    private final UUID refundRequestId;
    private final UUID adminId;
    //    private final UUID dispatchId;
    private final UUID tripId;
    private final UUID paymentId;
    private final UUID passengerId;
    private final String rejectReason;
    private final LocalDateTime rejectedAt;

    private RefundRejectedCommand(
            UUID notificationOriginId, UUID notifierId,
//            UUID dispatchId,
            UUID tripId,
            UUID paymentId,
            UUID passengerId,
            String rejectReason,
            LocalDateTime rejectedAt,
            Metadata metadata
    ) {
        super(notificationOriginId, notifierId, null, metadata);
        this.refundRequestId = notificationOriginId;
        this.adminId = notifierId;
//        this.dispatchId = dispatchId;
        this.tripId = tripId;
        this.paymentId = paymentId;
        this.passengerId = passengerId;
        this.rejectReason = rejectReason;
        this.rejectedAt = rejectedAt;
    }
    public static RefundRejectedCommand create(
            UUID notificationOriginId, UUID notifierId,
//            UUID dispatchId,
            UUID tripId,
            UUID paymentId,
            UUID passengerId,
            String rejectReason,
            LocalDateTime rejectedAt,
            Metadata metadata
    ) {
        return new RefundRejectedCommand(notificationOriginId, notifierId, tripId, paymentId, passengerId, rejectReason, rejectedAt, metadata);
    }

    public Notification toEntity(NotificationType notificationType) {
        return Notification.create(this.getNotificationOriginId(), this.getNotifierId(), notificationType, this.getMessage());
    }
}

