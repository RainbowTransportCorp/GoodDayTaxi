package com.gooddaytaxi.support.application.service;

import com.gooddaytaxi.support.domain.notification.model.NotificationType;
import com.gooddaytaxi.support.application.service.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationFilter(
        // 사용자
        UUID notifierId,
        UserRole role,
        // 도메인
        UUID notificationOriginId,
        // 알림 타입
        NotificationType notificationType,
        // 날짜
        LocalDateTime from,
        LocalDateTime to
    ) {}

