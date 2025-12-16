package com.gooddaytaxi.support.application.service;

import com.gooddaytaxi.support.adapter.out.internal.account.dto.UserRole;
import com.gooddaytaxi.support.application.port.in.web.GetAllUserNotificationsUsecase;
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
    @Override
    public void execute(UUID userId, String userRole) {
        log.debug("[Check] 사용자 ID: {}, 사용자 역할: {}", userRole, userId);

        UserRole role = UserRole.valueOf(userRole);

        switch(role) {
            case DRIVER -> {        // 기사

            }
            case PASSENGER -> {     // 승객

            }
            case ADMIN -> {         // 관리자(조회 권한)

            }
            case MASTER_ADMIN -> {  // 최고 관리자

            }
        }


    }
}
