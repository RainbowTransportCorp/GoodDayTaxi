package com.gooddaytaxi.support.application.dto.dispatch;

import com.gooddaytaxi.support.application.dto.Metadata;
import com.gooddaytaxi.support.application.dto.Command;
import com.gooddaytaxi.support.domain.notification.model.Notification;
import com.gooddaytaxi.support.domain.notification.model.NotificationType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 배차 거절 알림 Command
 * - DISPATCH_REJECTED 이벤트 처리
 */
@Getter
public class NotifyDispatchRejectedCommand extends Command {
    private final UUID dispatchId;
    private final UUID driverId;
    private final UUID passengerId;
    private final LocalDateTime rejectedAt;

    private NotifyDispatchRejectedCommand(
            UUID notificationOriginId,
            UUID notifierId,
            UUID driverId,
            UUID passengerId,
            String message,
            LocalDateTime rejectedAt,
            Metadata metadata
    ) {
        super(notificationOriginId, notifierId, message, metadata);
        this.dispatchId = notificationOriginId;
        this.driverId = driverId;
        this.passengerId = passengerId;
        this.rejectedAt = rejectedAt;
    }
    public static NotifyDispatchRejectedCommand create(
            UUID notificationOriginId,
            UUID notifierId,
            UUID driverId,
            UUID passengerId,
            String message,
            LocalDateTime rejectedAt,
            Metadata metadata
    ) {
        return new NotifyDispatchRejectedCommand(notificationOriginId, notifierId, driverId, passengerId, message, rejectedAt, metadata);
    }

    public Notification toEntity(NotificationType notificationType) {
        return Notification.create(this.getNotificationOriginId(), this.getNotifierId(), notificationType, this.getMessage());
    }
}