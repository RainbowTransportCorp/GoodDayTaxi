package com.gooddaytaxi.support.application.dto.dispatch;

import com.gooddaytaxi.support.application.dto.Metadata;
import com.gooddaytaxi.support.application.dto.Command;
import com.gooddaytaxi.support.domain.notification.model.Notification;
import com.gooddaytaxi.support.domain.notification.model.NotificationType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 배차 취소 알림 Command
 * - DISPATCH_CANCELLED 이벤트 처리
 */
@Getter
public class NotifyDispatchCanceledCommand extends Command {
    private final UUID dispatchId;
    private final UUID driverId;
    private final UUID passengerId;
    private final String canceledBy;
    private final LocalDateTime canceledAt;

    private NotifyDispatchCanceledCommand(
            UUID notificationOriginId,
            UUID notifierId,
            UUID driverId,
            UUID passengerId,
            String message,
            String canceledBy,
            LocalDateTime canceledAt,
            Metadata metadata
    ) {
        super(notificationOriginId, notifierId, message, metadata);
        this.dispatchId = notificationOriginId;
        this.driverId = driverId;
        this.passengerId = passengerId;
        this.canceledBy = canceledBy;
        this.canceledAt = canceledAt;
    }
    public static NotifyDispatchCanceledCommand create(
            UUID notificationOriginId,
            UUID notifierId,
            UUID driverId,
            UUID passengerId,
            String message,
            String canceledBy,
            LocalDateTime canceledAt,
            Metadata metadata
    ) {
        return new NotifyDispatchCanceledCommand(notificationOriginId, notifierId, driverId, passengerId, message, canceledBy, canceledAt, metadata);
    }

    public Notification toEntity(NotificationType notificationType) {
        return Notification.create(this.getNotificationOriginId(), this.getNotifierId(), notificationType, this.getMessage());
    }
}