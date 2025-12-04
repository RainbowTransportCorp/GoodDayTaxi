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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final GetUserDetailUseCase getUserDetailUseCase;
    private final ChangeUserStatusUseCase changeUserStatusUseCase;
    private final AdminDeleteUserUseCase adminDeleteUserUseCase;
    
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserListResponse>> getAllUsers(
            @RequestHeader("X-User-Role") String requestUserRole) {
        
        log.debug("전체 사용자 조회 요청: requestUserRole={}", requestUserRole);
        
        List<AdminUserListResponse> response = getAllUsersUseCase.execute(requestUserRole);
        
        log.debug("전체 사용자 조회 완료: userCount={}", response.size());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/users/{userId}")
    public ResponseEntity<AdminUserDetailResponse> getUserDetail(
            @RequestHeader("X-User-Role") String requestUserRole,
            @PathVariable("userId") String userId) {
        
        log.debug("사용자 상세 조회 요청: requestUserRole={}, userId={}", requestUserRole, userId);
        
        UUID userUuid = UUID.fromString(userId);
        AdminUserDetailResponse response = getUserDetailUseCase.execute(requestUserRole, userUuid);
        
        log.debug("사용자 상세 조회 완료: userId={}", userId);
        
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<ChangeUserStatusResponse> changeUserStatus(
            @RequestHeader("X-User-Role") String requestUserRole,
            @PathVariable("userId") String userId,
            @Valid @RequestBody ChangeUserStatusCommand command) {
        
        log.debug("사용자 상태 변경 요청: requestUserRole={}, userId={}, newStatus={}", 
                requestUserRole, userId, command.getStatus());
        
        UUID userUuid = UUID.fromString(userId);
        ChangeUserStatusResponse response = changeUserStatusUseCase.execute(requestUserRole, userUuid, command);
        
        log.debug("사용자 상태 변경 완료: userId={}, newStatus={}", userId, response.getStatus());
        
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/users/{userId}/delete")
    public ResponseEntity<DeleteUserResponse> deleteUser(
            @RequestHeader("X-User-Role") String requestUserRole,
            @PathVariable("userId") String userId,
            @Valid @RequestBody DeleteUserCommand command) {
        
        log.debug("관리자 사용자 삭제 요청: requestUserRole={}, userId={}, reason={}", 
                requestUserRole, userId, command.getReason());
        
        UUID userUuid = UUID.fromString(userId);
        DeleteUserResponse response = adminDeleteUserUseCase.execute(requestUserRole, userUuid, command);
        
        log.debug("관리자 사용자 삭제 완료: userId={}, deletedAt={}", userId, response.getDeletedAt());
        
        return ResponseEntity.ok(response);
    }
}