package com.gooddaytaxi.support.adapter.in.web.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.support.application.dto.notification.NotificationResponse;
import com.gooddaytaxi.support.application.port.in.web.admin.GetAllNotificationForAdminUsecase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "알림 관리", description = "알림 조회 및 읽음 처리 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/supports")
@RequiredArgsConstructor
public class AdminController {

    private final GetAllNotificationForAdminUsecase getAllNotificationForAdminUsecase;

    /*
     * 관리자의 전체 알림 목록 조회(필터링, 페이징)
     * */
//    @Operation(summary = "관리자의 알림 전체 조회", description = "관리자만 모든 알림 목록을 조회합니다.")
//    @GetMapping("/notifications")
//    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getAllNotifications(
//            @Parameter(description = "사용자 ID (UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
//            @RequestHeader("X-User-UUID") String userId,
//            @Parameter(description = "사용자 역할 (ADMIN/MASTER_ADMIN/Driver/Passenger)", required = true, example = "DRIVER")
//            @RequestHeader("X-User-Role") String userRole
//    ) {
//        log.debug("[Check] 사용자 ID: {}, 사용자 역할: {}", userId, userRole);
//
//        // 알림 목록 조회
//        Page<NotificationResponse> response = getAllNotificationForAdminUsecase.execute(UUID.fromString(userId), userRole);
//
//
//        log.debug("[Response] 모든 알림 조회 완료");
//
//        return ResponseEntity.ok(ApiResponse.success(response, "전체 알림 조회가 완료되었습니다"));
//    }

}
