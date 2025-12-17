package com.gooddaytaxi.support.application.port.in.web.admin;

import com.gooddaytaxi.support.application.dto.notification.NotificationResponse;

import java.util.List;
import java.util.UUID;

public interface GetAllNotificationForAdminUsecase {
    List<NotificationResponse> execute(UUID userId, String userRole);
}
