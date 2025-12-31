package com.gooddaytaxi.support.application.port.in.web.notification;

import java.util.UUID;

public interface DeleteNotificationUsecase {
    void execute(UUID userId, String userRole, UUID notificationId);
}
