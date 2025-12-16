package com.gooddaytaxi.support.adapter.in.web.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.support.adapter.in.web.dto.NotificationResponse;
import com.gooddaytaxi.support.application.port.in.web.GetAllUserNotificationsUsecase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


//@Tag(name = "알림 관리", description = "알림 조회 및 읽음 처리 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/support")
@RequiredArgsConstructor
public class NotificationController {

    private final GetAllUserNotificationsUsecase getAllUserNotificationsUsecase;

    @Operation(summary = "사용자 알림 전체 조회", description = "사용자 본인의 알림 목록을 조회합니다.")
    @GetMapping("/notifications/me")
    public ResponseEntity<ApiResponse<NotificationResponse>> getAllMyNotifications(
            @Parameter(description = "사용자 역할 (ADMIN/MASTER_ADMIN/Driver/Passenger)", required = true, example = "DRIVER")
            @RequestHeader("X-User-Role") String userRole,
            @Parameter(description = "사용자 ID (UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestHeader("X-User-UUID") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.debug("[Check] 사용자 ID: {}, 사용자 역할: {}", userRole, userId);

        UUID userUuid = UUID.fromString(userId);
        getAllUserNotificationsUsecase.execute(userUuid, userRole);
        NotificationResponse response = null;
        log.debug("[Response] 사용자 모든 알림 조회 완료");

        return ResponseEntity.ok(ApiResponse.success(response, "사용자의 전체 알림 조회가 완료되었습니다"));
    }
}


