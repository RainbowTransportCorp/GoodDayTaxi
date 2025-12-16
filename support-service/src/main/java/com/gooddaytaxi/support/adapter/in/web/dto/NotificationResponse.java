package com.gooddaytaxi.support.adapter.in.web.dto;

import com.gooddaytaxi.support.domain.notification.model.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse (
    UUID notificationId,
    UUID notifierId,
    UUID notificationOriginId,
    NotificationType notificationType,
    LocalDateTime notifiedAt,
    UUID dispatchId,
    UUID tripId,
    UUID paymentId,
    UUID driverId,
    UUID passengerId,
    String message,
    Boolean isRead
) {}