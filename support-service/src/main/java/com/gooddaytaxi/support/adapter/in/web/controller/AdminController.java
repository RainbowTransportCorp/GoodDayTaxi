package com.gooddaytaxi.support.adapter.in.web.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.support.application.dto.output.log.LogResponse;
import com.gooddaytaxi.support.application.dto.output.notification.NotificationResponse;
import com.gooddaytaxi.support.application.port.in.web.notification.DeleteNotificationUsecase;
import com.gooddaytaxi.support.application.port.in.web.notification.GetAllNotificationUsecase;
import com.gooddaytaxi.support.application.port.in.web.log.GetAllLogsUsecase;
import com.gooddaytaxi.support.application.query.filter.AdminLogFilter;
import com.gooddaytaxi.support.application.query.filter.AdminNotificationFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "알림 관리", description = "알림 조회 및 읽음 처리 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/supports")
@RequiredArgsConstructor
public class AdminController {

    private final GetAllNotificationUsecase getAllNotificationUsecase;
    private final GetAllLogsUsecase getAllLogUsecase;
    private final DeleteNotificationUsecase deleteNotificationUsecase;

    /**
     * 관리자의 전체 알림 목록 조회(필터링, 페이징)
     */
    @Operation(summary = "관리자의 알림 전체 조회", description = "관리자만 모든 알림 목록을 조회합니다.")
    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getAllNotifications(
            @Parameter(description = "사용자 ID (UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestHeader("X-User-UUID") String userId,
            @Parameter(description = "사용자 역할 (ADMIN/MASTER_ADMIN/Driver/Passenger)", required = true, example = "MASTER_ADMIN")
            @RequestHeader("X-User-Role") String userRole,

            // 필터 파라미터
            @RequestParam(required = false) String notificationOriginId,
            @RequestParam(required = false) String notifierId,
            @RequestParam(required = false) String notificationType,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            Pageable pageable
    ) {
        log.debug("[Check] 사용자 ID: {}, 사용자 역할: {}", userId, userRole);
        log.debug("[Check] 필터링 조건 파라미터 알림 통지자: {}, 도메인 ID: {}, 알림 타입: {}, from: {}, to: {}", notifierId, notificationOriginId, notificationType, from, to);

        // 필터링 조건 생성
        AdminNotificationFilter filter =
                new AdminNotificationFilter(notificationOriginId, notifierId, notificationType, from, to);
        // 알림 목록 조회
        Page<NotificationResponse> response =
                getAllNotificationUsecase.execute(
                        UUID.fromString(userId), userRole,
                        filter,
                        pageable);

        log.debug("[Response] 모든 알림 조회 완료");

        return ResponseEntity.ok(ApiResponse.success(response, "전체 알림 조회가 완료되었습니다"));
    }

    /**
     * 관리자의 전체 로그 목록 조회(필터링, 페이징)
     */
    @Operation(summary = "관리자의 로그 전체 조회", description = "관리자만 모든 로그 목록을 조회합니다.")
    @GetMapping("/monitoring")
    public ResponseEntity<ApiResponse<Page<LogResponse>>> getAllLogs(
            @Parameter(description = "사용자 ID (UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestHeader("X-User-UUID") String userId,
            @Parameter(description = "사용자 역할 (ADMIN/MASTER_ADMIN/Driver/Passenger)", required = true, example = "MASTER_ADMIN")
            @RequestHeader("X-User-Role") String userRole,

            // 필터 파라미터
            @RequestParam(required = false) String logType,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            Pageable pageable
    ) {
        log.debug("[Check] 사용자 ID: {}, 사용자 역할: {}", userId, userRole);
        log.debug("[Check] 필터링 조건 파라미터 로그 타입: {}, from: {}, to: {}", logType, from, to);

        // 필터링 조건 생성
        AdminLogFilter filter =
                new AdminLogFilter(logType, from, to);
        // 로그 목록 조회
        Page<LogResponse> response =
                getAllLogUsecase.execute(
                        UUID.fromString(userId), userRole,
                        filter,
                        pageable);

        log.debug("[Response] 모든 로그 조회 완료");

        return ResponseEntity.ok(ApiResponse.success(response, "전체 로그 조회가 완료되었습니다"));
    }

    /**
     * 관리자의 알림 삭제
     */
    @Operation(summary = "관리자의 알림 삭제", description = "관리자만 알림 삭제가 가능합니다.")
    @GetMapping("/notifications/{notificationId}/delete")
    public ResponseEntity<ApiResponse<NotificationResponse>> getAllNotifications(
            @Parameter(description = "사용자 ID (UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestHeader("X-User-UUID") String userId,
            @Parameter(description = "사용자 역할 (ADMIN/MASTER_ADMIN/Driver/Passenger)", required = true, example = "MASTER_ADMIN")
            @RequestHeader("X-User-Role") String userRole,
            @Parameter(description = "알림 ID (UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String notificationId
    ) {
        log.debug("[Check] 사용자 ID: {}, 사용자 역할: {}", userId, userRole);

        // 알림 삭제
        deleteNotificationUsecase.execute(UUID.fromString(userId), userRole, UUID.fromString(notificationId));

        log.debug("[Response] 알림 삭제 완료");

        return ResponseEntity.ok(ApiResponse.success(null, "알림이 삭제되었습니다."));
    }

}
