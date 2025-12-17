package com.gooddaytaxi.support.application.service;

import com.gooddaytaxi.support.application.dto.notification.NotificationResponse;
import com.gooddaytaxi.support.application.port.in.web.admin.GetAllNotificationForAdminUsecase;
import com.gooddaytaxi.support.application.port.out.persistence.NotificationQueryPersistencePort;
import com.gooddaytaxi.support.domain.exception.SupportBusinessException;
import com.gooddaytaxi.support.domain.exception.SupportErrorCode;
import com.gooddaytaxi.support.domain.notification.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 관리자 알림 서비스
 * Usecase 구현
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminService implements GetAllNotificationForAdminUsecase {

    private final NotificationQueryPersistencePort notificationQueryPersistencePort;
    private static final UUID SYSTEM_UUID = UUID.fromString("99999999-9999-9999-9999-999999999999");  // DISPATCH_TIMEOUT 이벤트 notifierId

    /**
     * 관리자의 알림 전체 조회 서비스
     */
    @Transactional(readOnly = true)
    @Override
    public List<NotificationResponse> execute(UUID userId, String userRole) {
        log.debug("[Check] 사용자 ID: {}, 사용자 역할: {}", userId, userRole);

        UserRole role = UserRole.valueOf(userRole);

        if (!(role.equals(UserRole.ADMIN) || role.equals(UserRole.MASTER_ADMIN))) {
            throw new SupportBusinessException(SupportErrorCode.ACCESS_DENIED);
        }

        // 모든 알림 조회
        List<Notification> notifications = notificationQueryPersistencePort.findAll();

        return notifications.stream().map(NotificationResponse::from).toList();

    }
}
