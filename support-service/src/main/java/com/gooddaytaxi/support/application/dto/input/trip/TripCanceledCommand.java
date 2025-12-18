package com.gooddaytaxi.support.application.dto.input.trip;

import com.gooddaytaxi.support.application.dto.input.Command;
import com.gooddaytaxi.support.application.dto.Metadata;
import com.gooddaytaxi.support.domain.notification.model.Notification;
import com.gooddaytaxi.support.domain.notification.model.NotificationType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 운행 취소 알림 Command
 * - TRIP_CANCELED 이벤트 처리
 */
@Getter
public class TripCanceledCommand extends Command {
    private final UUID tripId;
    private final UUID dispatchId;
    private final UUID driverId;
    private final UUID passengerId;
    private final String cancelReason;
    private final LocalDateTime canceledAt;

    private TripCanceledCommand(
            UUID notificationOriginId, UUID notifierId,
            UUID dispatchId,
            UUID driverId, UUID passengerId,
            String cancelReason,
            LocalDateTime canceledAt,
            Metadata metadata
    ) {
        super(notificationOriginId, notifierId, null, metadata);
        this.tripId = notificationOriginId;
        this.dispatchId = dispatchId;
        this.driverId = driverId;
        this.passengerId = passengerId;
        this.cancelReason = cancelReason;
        this.canceledAt = canceledAt;
    }

    public static TripCanceledCommand create(
            UUID notificationOriginId, UUID notifierId,
            UUID dispatchId,
            UUID driverId, UUID passengerId,
            String cancelReason,
            LocalDateTime canceledAt,
            Metadata metadata
    ) {
        return new TripCanceledCommand(notificationOriginId, notifierId, dispatchId, driverId, passengerId, cancelReason, canceledAt, metadata);
    }

    public Notification toEntity(NotificationType notificationType) {
        return Notification.create(this.getNotificationOriginId(), this.getNotifierId(), notificationType, this.getMessage());
    }
}
