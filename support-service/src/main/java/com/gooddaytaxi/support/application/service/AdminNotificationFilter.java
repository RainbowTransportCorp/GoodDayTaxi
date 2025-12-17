package com.gooddaytaxi.support.application.service;

import com.gooddaytaxi.support.domain.notification.model.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminNotificationFilter(
        // 도메인
        String notificationOriginId,
        // 사용자
        String notifierId,
        // 알림 타입
        String notificationType,
        // 날짜
        String from,
        String to
    ) {}

