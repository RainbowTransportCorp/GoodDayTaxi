package com.gooddaytaxi.support.application.service;

public record AdminNotificationFilter(
        // 도메인
        String notificationOriginId,
        // 사용자
        // 알림 타입
        String notificationType,
        // 날짜
        String from,
        String to
    ) {}

