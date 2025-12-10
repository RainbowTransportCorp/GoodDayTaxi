package com.gooddaytaxi.account.adapter.web;

import com.gooddaytaxi.account.application.dto.AdminUserDetailResponse;
import com.gooddaytaxi.account.application.dto.AdminUserListResponse;
import com.gooddaytaxi.account.application.dto.ChangeUserStatusCommand;
import com.gooddaytaxi.account.application.dto.ChangeUserStatusResponse;
import com.gooddaytaxi.account.application.dto.DeleteUserCommand;
import com.gooddaytaxi.account.application.dto.DeleteUserResponse;
import com.gooddaytaxi.account.application.usecase.AdminDeleteUserUseCase;
import com.gooddaytaxi.account.application.usecase.ChangeUserStatusUseCase;
import com.gooddaytaxi.account.application.usecase.GetAllUsersUseCase;
import com.gooddaytaxi.account.application.usecase.GetUserDetailUseCase;
import com.gooddaytaxi.common.core.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "관리자", description = "관리자 전용 사용자 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final GetUserDetailUseCase getUserDetailUseCase;
    private final ChangeUserStatusUseCase changeUserStatusUseCase;
    private final AdminDeleteUserUseCase adminDeleteUserUseCase;
    
    @Operation(summary = "전체 사용자 조회", description = "관리자 전용 API. 전체 사용자(승객/기사/관리자) 목록을 조회합니다. 사용자 기본 정보를 포함합니다.")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<AdminUserListResponse>>> getAllUsers(
            @Parameter(description = "요청자 역할 (ADMIN만 허용)", required = true, example = "ADMIN")
            @RequestHeader("X-User-Role") String requestUserRole) {
        
        log.debug("전체 사용자 조회 요청: requestUserRole={}", requestUserRole);
        
        List<AdminUserListResponse> response = getAllUsersUseCase.execute(requestUserRole);
        
        log.debug("전체 사용자 조회 완료: userCount={}", response.size());
        
        return ResponseEntity.ok(ApiResponse.success(response, "전체 사용자 조회가 완료되었습니다."));
    }
    
    @Operation(summary = "사용자 상세 조회", description = "관리자 전용 API. 특정 사용자(승객/기사/관리자)의 상세 정보를 조회합니다. 생성/수정 시간 포함.")
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<AdminUserDetailResponse>> getUserDetail(
            @Parameter(description = "요청자 역할 (ADMIN만 허용)", required = true, example = "ADMIN")
            @RequestHeader("X-User-Role") String requestUserRole,
            @Parameter(description = "조회할 사용자 UUID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @PathVariable("userId") String userId) {
        
        log.debug("사용자 상세 조회 요청: requestUserRole={}, userId={}", requestUserRole, userId);
        
        UUID userUuid = UUID.fromString(userId);
        AdminUserDetailResponse response = getUserDetailUseCase.execute(requestUserRole, userUuid);
        
        log.debug("사용자 상세 조회 완료: userId={}", userId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "사용자 상세 조회가 완료되었습니다."));
    }
    
    @Operation(summary = "사용자 상태 변경", description = "관리자 전용 API. 특정 사용자(승객/기사/관리자)의 활성/비활성 상태를 변경합니다. 정지/해제 처리에 사용.")
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<ChangeUserStatusResponse>> changeUserStatus(
            @Parameter(description = "요청자 역할 (ADMIN만 허용)", required = true, example = "ADMIN")
            @RequestHeader("X-User-Role") String requestUserRole,
            @Parameter(description = "조회할 사용자 UUID", required = true, example = "550e8400-e29b-41d4-a716-446655440003")
            @PathVariable("userId") String userId,
            @Valid @RequestBody ChangeUserStatusCommand command) {
        
        log.debug("사용자 상태 변경 요청: requestUserRole={}, userId={}, newStatus={}", 
                requestUserRole, userId, command.getStatus());
        
        UUID userUuid = UUID.fromString(userId);
        ChangeUserStatusResponse response = changeUserStatusUseCase.execute(requestUserRole, userUuid, command);
        
        log.debug("사용자 상태 변경 완료: userId={}, newStatus={}", userId, response.getStatus());
        
        return ResponseEntity.ok(ApiResponse.success(response, "사용자 상태 변경이 완료되었습니다."));
    }
    
    @Operation(summary = "사용자 삭제", description = "관리자 전용 API. 특정 사용자(승객/기사/관리자)를 삭제 처리합니다. 삭제 사유 필수, Soft Delete로 처리됩니다.")
    @PatchMapping("/users/{userId}/delete")
    public ResponseEntity<ApiResponse<DeleteUserResponse>> deleteUser(
            @Parameter(description = "요청자 역할 (ADMIN만 허용)", required = true, example = "ADMIN")
            @RequestHeader("X-User-Role") String requestUserRole,
            @Parameter(description = "삭제할 사용자 UUID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @PathVariable("userId") String userId,
            @Valid @RequestBody DeleteUserCommand command) {
        
        log.debug("관리자 사용자 삭제 요청: requestUserRole={}, userId={}, reason={}", 
                requestUserRole, userId, command.getReason());
        
        UUID userUuid = UUID.fromString(userId);
        DeleteUserResponse response = adminDeleteUserUseCase.execute(requestUserRole, userUuid, command);
        
        log.debug("관리자 사용자 삭제 완료: userId={}, deletedAt={}", userId, response.getDeletedAt());
        
        return ResponseEntity.ok(ApiResponse.success(response, "사용자 삭제 처리가 완료되었습니다."));
    }
}