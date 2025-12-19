package com.gooddaytaxi.support.application.dto.output.notification;

import com.gooddaytaxi.support.domain.notification.model.NotificationType;
import com.gooddaytaxi.support.domain.notification.model.Notification;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse (
    UUID notificationId,
    UUID notificationOriginId,
    UUID notifierId,
    NotificationType notificationType,
    UUID dispatchId,
    UUID tripId,
    UUID paymentId,
    UUID driverId,
    UUID passengerId,
    String message,
    LocalDateTime notifiedAt,
    boolean isRead
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getNotificationOriginId(),
                n.getNotifierId(),
                n.getNotificationType(),
                n.getDispatchId(),
                n.getTripId(),
                n.getPaymentId(),
                n.getDriverId(),
                n.getPassengerId(),
                n.getMessage(),
                n.getNotifiedAt(),
                n.isRead()
        );
    }
}