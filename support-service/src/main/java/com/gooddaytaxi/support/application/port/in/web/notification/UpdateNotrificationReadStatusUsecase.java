package com.gooddaytaxi.support.application.port.in.web.notification;

import com.gooddaytaxi.support.application.dto.output.notification.NotificationResponse;

import java.util.UUID;

public interface UpdateNotrificationReadStatusUsecase {
    NotificationResponse execute(UUID userId, UUID notificationId);
}
