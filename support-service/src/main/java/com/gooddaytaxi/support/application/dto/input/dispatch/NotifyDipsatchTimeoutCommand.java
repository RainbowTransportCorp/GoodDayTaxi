package com.gooddaytaxi.support.application.dto.input.dispatch;

import com.gooddaytaxi.support.application.dto.Metadata;
import com.gooddaytaxi.support.application.dto.input.Command;
import com.gooddaytaxi.support.domain.notification.model.Notification;
import com.gooddaytaxi.support.domain.notification.model.NotificationType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 배차 시간 초과 정보 알림 Command
 * - DISPATCH_TIMEOUT 이벤트 처리
 */
@Getter
public class NotifyDipsatchTimeoutCommand extends Command {
    private final UUID dispatchId;
    private final UUID passengerId;
    private final LocalDateTime timeoutAt;

    private NotifyDipsatchTimeoutCommand(
            UUID notificationOriginId,
            UUID notifierId,
            UUID passengerId,
            String message,
            LocalDateTime timeoutAt,
            Metadata metadata
    ) {
        super(notificationOriginId, notifierId, message, metadata);
        this.dispatchId = notificationOriginId;
        this.passengerId = passengerId;
        this.timeoutAt = timeoutAt;
    }
    public static NotifyDipsatchTimeoutCommand create(
            UUID notificationOriginId,
            UUID notifierId,
            UUID passengerId,
            String message,
            LocalDateTime timeoutAt,
            Metadata metadata
    ) {
        return new NotifyDipsatchTimeoutCommand(notificationOriginId, notifierId, passengerId, message, timeoutAt, metadata);
    }

    public Notification toEntity(NotificationType notificationType) {
        return Notification.create(this.getNotificationOriginId(), this.getNotifierId(), notificationType, this.getMessage());
    }
}
