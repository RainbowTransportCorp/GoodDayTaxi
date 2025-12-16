package com.gooddaytaxi.support.application.service;

import com.gooddaytaxi.support.adapter.out.internal.account.dto.UserRole;
import com.gooddaytaxi.support.application.dto.notification.NotificationResponse;
import com.gooddaytaxi.support.application.port.in.web.GetAllUserNotificationsUsecase;
import com.gooddaytaxi.support.application.port.out.persistence.NotificationQueryPersistencePort;
import com.gooddaytaxi.support.domain.notification.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 사용자 알림 서비스
 * Usecase 구현
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService implements GetAllUserNotificationsUsecase {

    private final NotificationQueryPersistencePort notificationQueryPersistencePort;
    private static final UUID SYSTEM_UUID = UUID.fromString("99999999-9999-9999-9999-999999999999");  // DISPATCH_TIMEOUT 이벤트 notifierId

    /*
    * 사용자의 알림 전체 조회 서비스
    * */
    @Transactional(readOnly = true)
    @Override
    public List<NotificationResponse> execute(UUID userId, String userRole) {
        log.debug("[Check] 사용자 ID: {}, 사용자 역할: {}", userRole, userId);

        UserRole role = UserRole.valueOf(userRole);

        // Notifier ID로 알림 조회
        List<Notification> notifications;
        switch(role) {
            case DRIVER, PASSENGER -> {   // 기사, 승객일 경우
                notifications = notificationQueryPersistencePort.findAllByNotifierId(userId);
            }
            case ADMIN, MASTER_ADMIN -> { // 관리자일 경우, System이 통지한 알림 포함하여 조회
                notifications = notificationQueryPersistencePort.findAllByNotifierIdIn(List.of(userId, SYSTEM_UUID));
            }
            default -> {
                notifications = notificationQueryPersistencePort.findAllByNotifierId(userId);
            }
        }

        return notifications.stream().map(NotificationResponse::from).toList();
    }
}
