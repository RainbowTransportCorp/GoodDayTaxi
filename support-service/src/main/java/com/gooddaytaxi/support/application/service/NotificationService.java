package com.gooddaytaxi.support.application.service;

import com.gooddaytaxi.support.adapter.out.internal.account.dto.UserRole;
import com.gooddaytaxi.support.application.port.in.web.GetAllUserNotificationsUsecase;
import com.gooddaytaxi.support.application.port.out.persistence.NotificationQueryPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public void execute(UUID userId, String userRole) {
        log.debug("[Check] 사용자 ID: {}, 사용자 역할: {}", userRole, userId);

        UserRole role = UserRole.valueOf(userRole);


//        List<Notification> notifications;
//        switch(role) {
//            case DRIVER, PASSENGER -> {   // 기사, 승객
//                notifications = notificationQueryPersistencePort.findAllByNotifierId(userId);
//            }
//            case ADMIN, MASTER_ADMIN -> { // 관리자
////                notifications = notificationQueryPersistencePort.findAllByNotifierIdIn();
//                List<Notification> systemNotifications = notificationQueryPersistencePort.findAllByNotifierId(SYSTEM_UUID);
//            }
//            default -> {
//                notifications = notificationQueryPersistencePort.findAllByNotifierId(userId);
//            }
//        }


    }
}
