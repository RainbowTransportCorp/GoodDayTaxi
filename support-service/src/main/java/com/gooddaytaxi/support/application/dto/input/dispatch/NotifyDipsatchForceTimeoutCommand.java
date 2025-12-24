package com.gooddaytaxi.support.application.dto.input.dispatch;

import com.gooddaytaxi.support.application.dto.Metadata;
import com.gooddaytaxi.support.application.dto.input.Command;
import com.gooddaytaxi.support.domain.notification.model.Notification;
import com.gooddaytaxi.support.domain.notification.model.NotificationType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 운행 시작 직전 관리자의 강제 타임아웃으로 인한 운행 중지 알림 Command
 * - DISPATCH_FORCE_TIMEOUT 이벤트 처리
 */
@Getter
public class NotifyDipsatchForceTimeoutCommand extends Command {
    private final UUID dispatchId;
    private final UUID driverId;
    private final String previousStatus;
    private final LocalDateTime forceTimeoutAt;
    private final String reason;
    private final boolean tripRequestMayHaveBeenSent;

    private NotifyDipsatchForceTimeoutCommand(
            UUID notificationOriginId,
            UUID notifierId,
            UUID driverId,
            String previousStatus,
            LocalDateTime forceTimeoutAt,
            String reason,
            String message,
            boolean tripRequestMayHaveBeenSent,
            Metadata metadata
    ) {
        super(notificationOriginId, notifierId, message, metadata);
        this.dispatchId = notificationOriginId;
        this.driverId = driverId;
        this.previousStatus = previousStatus;
        this.forceTimeoutAt = forceTimeoutAt;
        this.reason = reason;
        this.tripRequestMayHaveBeenSent = tripRequestMayHaveBeenSent;
    }
    public static NotifyDipsatchForceTimeoutCommand create(
            UUID notificationOriginId,
            UUID notifierId,
            UUID driverId,
            String previousStatus,
            LocalDateTime forceTimeoutAt,
            String reason,
            String message,
            boolean tripRequestMayHaveBeenSent,
            Metadata metadata
    ) {
        return new NotifyDipsatchForceTimeoutCommand(notificationOriginId, notifierId, driverId, previousStatus, forceTimeoutAt, reason, message, tripRequestMayHaveBeenSent, metadata);
    }

    public Notification toEntity(NotificationType notificationType) {
        return Notification.create(this.getNotificationOriginId(), this.getNotifierId(), notificationType, this.getMessage());
    }
}
