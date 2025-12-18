package com.gooddaytaxi.support.application.service;

import com.gooddaytaxi.support.application.dto.log.LogResponse;
import com.gooddaytaxi.support.application.dto.notification.NotificationResponse;
import com.gooddaytaxi.support.application.port.in.web.admin.DeleteNotificationUsecase;
import com.gooddaytaxi.support.application.port.in.web.admin.GetAllNotificationUsecase;
import com.gooddaytaxi.support.application.port.in.web.log.GetAllLogsUsecase;
import com.gooddaytaxi.support.application.port.out.persistence.LogCommandPersistencePort;
import com.gooddaytaxi.support.application.port.out.persistence.LogQueryPersistencePort;
import com.gooddaytaxi.support.application.port.out.persistence.NotificationQueryPersistencePort;
import com.gooddaytaxi.support.domain.exception.SupportBusinessException;
import com.gooddaytaxi.support.domain.exception.SupportErrorCode;
import com.gooddaytaxi.support.domain.log.model.Log;
import com.gooddaytaxi.support.domain.notification.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 관리자 알림 서비스
 * Usecase 구현
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService implements GetAllNotificationUsecase, GetAllLogsUsecase, DeleteNotificationUsecase {

    private final NotificationQueryPersistencePort notificationQueryPersistencePort;
    private final LogQueryPersistencePort logQueryPersistencePort;
    
    /**
     * 관리자의 알림 전체 조회 서비스
     */
    @Transactional(readOnly = true)
    @Override
    public Page<NotificationResponse> execute(UUID userId, String userRole, AdminNotificationFilter filter, Pageable pageable) {
        log.debug("[Check] 사용자 ID: {}, 사용자 역할: {}", userId, userRole);

        UserRole role = UserRole.valueOf(userRole);

        if (!(role.equals(UserRole.ADMIN) || role.equals(UserRole.MASTER_ADMIN))) {
            throw new SupportBusinessException(SupportErrorCode.ACCESS_DENIED);
        }

        // 모든 알림 조회
        Page<Notification> notifications = notificationQueryPersistencePort.search(filter, pageable);

        return notifications.map(NotificationResponse::from);

    }



    /**
     * 관리자의 로그 전체 조회 서비스
     */
    @Transactional(readOnly = true)
    @Override
    public Page<LogResponse> execute(UUID userId, String userRole, AdminLogFilter filter, Pageable pageable) {
        log.debug("[Check] 사용자 ID: {}, 사용자 역할: {}", userId, userRole);

        UserRole role = UserRole.valueOf(userRole);

        if (!(role.equals(UserRole.ADMIN) || role.equals(UserRole.MASTER_ADMIN))) {
            throw new SupportBusinessException(SupportErrorCode.ACCESS_DENIED);
        }

        // 모든 로그 조회
        Page<Log> logs = logQueryPersistencePort.search(filter, pageable);

        return logs.map(LogResponse::from);
    }

    /**
     * 관리자의 알림 삭제 서비스
     */
    @Transactional
    @Override
    public void execute(UUID userId, String userRole, UUID notificationId) {
        log.debug("[Check] 사용자 ID: {}, 사용자 역할: {}", userId, userRole);

        UserRole role = UserRole.valueOf(userRole);

        if (!(role.equals(UserRole.MASTER_ADMIN))) {
            throw new SupportBusinessException(SupportErrorCode.ACCESS_DENIED);
        }

        // 알림 삭제
        Notification notification = notificationQueryPersistencePort.findById(notificationId);
        notification.softDelete(userId);
    }
}
