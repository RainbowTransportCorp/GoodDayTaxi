package com.gooddaytaxi.support.application.query.filter;

/**
 * 관리자의 전체 알림 조회를 위한 검색 필터 기준
 */
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

