package com.gooddaytaxi.support.application.port.in.web.admin;

import com.gooddaytaxi.support.application.dto.notification.NotificationResponse;
import com.gooddaytaxi.support.application.service.AdminNotificationFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetAllNotificationUsecase {
    Page<NotificationResponse> execute(
            UUID userId, String userRole,
            AdminNotificationFilter filter, // 필터링
            Pageable pageable               // 페이지네이션
    );
}
