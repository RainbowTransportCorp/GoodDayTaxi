package com.gooddaytaxi.support.adapter.in.web.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.support.application.dto.notification.NotificationResponse;
import com.gooddaytaxi.support.application.port.in.web.GetAllUserNotificationsUsecase;
import com.gooddaytaxi.support.application.port.in.web.UpdateNotrificationAsReadUsecase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@Tag(name = "알림 관리", description = "알림 조회 및 읽음 처리 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/supports")
@RequiredArgsConstructor
public class NotificationController {

    private final GetAllUserNotificationsUsecase getAllUserNotificationsUsecase;
    private final UpdateNotrificationAsReadUsecase updateNotrificationAsReadUsecase;

    /*
    * 사용자 본인 알림 전체 조회
    * */
    @Operation(summary = "사용자 알림 전체 조회", description = "사용자 본인의 알림 목록을 조회합니다.")
    @GetMapping("/notifications/me")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getAllMyNotifications(
            @Parameter(description = "사용자 ID (UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestHeader("X-User-UUID") String userId,
            @Parameter(description = "사용자 역할 (ADMIN/MASTER_ADMIN/Driver/Passenger)", required = true, example = "DRIVER")
            @RequestHeader("X-User-Role") String userRole
            ) {
        log.debug("[Check] 사용자 ID: {}, 사용자 역할: {}", userId, userRole);

        // 알림 목록 조회
        List<NotificationResponse> response = getAllUserNotificationsUsecase.execute(UUID.fromString(userId), userRole);

        log.debug("[Response] 사용자 모든 알림 조회 완료");

        return ResponseEntity.ok(ApiResponse.success(response, "사용자의 전체 알림 조회가 완료되었습니다"));
    }

    /*
     * 사용자 알림 읽음 처리
     * */
    @Operation(summary = "사용자 알림 읽음 처리", description = "전송된 알림을 읽음 처리합니다.")
    @PatchMapping("/notifications/{notificationId}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markNotificationAsRead(
            @Parameter(description = "사용자 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestHeader("X-User-UUID") String userId,
            @Parameter(description = "Notification ID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String notificationId) {

        log.debug("[Check] 사용자 ID: {}", userId);

        // 알림 목록 조회
        NotificationResponse response = updateNotrificationAsReadUsecase.execute(UUID.fromString(userId), UUID.fromString(notificationId));

        log.debug("[Response] 알림 읽음 처리 완료");

        return ResponseEntity.ok(ApiResponse.success(response, "알림 메시지가 '읽음'으로 변경되었습니다"));
    }
}


