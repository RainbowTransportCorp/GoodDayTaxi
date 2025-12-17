package com.gooddaytaxi.support.application.port.in.web;

import com.gooddaytaxi.support.application.dto.notification.NotificationResponse;

import java.util.UUID;

public interface UpdateNotrificationAsReadUsecase {
    NotificationResponse execute(UUID userId, UUID notificationId);
}
