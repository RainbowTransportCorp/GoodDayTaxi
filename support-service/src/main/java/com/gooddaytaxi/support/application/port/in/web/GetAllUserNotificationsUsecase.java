package com.gooddaytaxi.support.application.port.in.web;

import java.util.UUID;

public interface GetAllUserNotificationsUsecase {
    void execute(UUID userId, String userRole);
}
