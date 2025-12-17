package com.gooddaytaxi.account.adapter.web;

import com.gooddaytaxi.account.application.dto.UpdateUserProfileCommand;
import com.gooddaytaxi.account.application.dto.UpdateUserProfileResponse;
import com.gooddaytaxi.account.application.dto.UserProfileResponse;
import com.gooddaytaxi.account.application.usecase.DeleteUserUseCase;
import com.gooddaytaxi.account.application.usecase.GetUserProfileUseCase;
import com.gooddaytaxi.account.application.usecase.UpdateUserProfileUseCase;
import com.gooddaytaxi.common.core.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "사용자 관리", description = "사용자 프로필 조회 및 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final GetUserProfileUseCase getUserProfileUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    
    @Operation(summary = "내 정보 조회", description = "현재 로그인된 사용자(승객/기사/관리자)의 프로필 정보를 조회합니다")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자 없음")
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(
            @Parameter(description = "사용자 UUID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @RequestHeader("X-User-UUID") String userUuidHeader) {
        
        log.debug("내 정보 조회 요청: userUuid={}", userUuidHeader);
        
        UUID userUuid = UUID.fromString(userUuidHeader);
        UserProfileResponse response = getUserProfileUseCase.execute(userUuid);
        
        log.debug("내 정보 조회 완료: userUuid={}", userUuid);
        
        return ResponseEntity.ok(ApiResponse.success(response, "내 정보 조회가 완료되었습니다."));
    }
    
    @Operation(summary = "내 정보 수정", description = "현재 로그인된 사용자(승객/기사/관리자)의 기본 프로필 정보(이름, 전화번호)를 수정합니다")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UpdateUserProfileResponse>> updateMyProfile(
            @Parameter(description = "사용자 UUID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @RequestHeader("X-User-UUID") String userUuidHeader,
            @Valid @RequestBody UpdateUserProfileCommand command) {
        
        log.debug("내 정보 수정 요청: userUuid={}, name={}", userUuidHeader, command.getName());
        
        UUID userUuid = UUID.fromString(userUuidHeader);
        UpdateUserProfileResponse response = updateUserProfileUseCase.execute(userUuid, command);
        
        log.debug("내 정보 수정 완료: userUuid={}", userUuid);
        
        return ResponseEntity.ok(ApiResponse.success(response, "내 정보 수정이 완료되었습니다."));
    }
    
    @Operation(summary = "회원 탈퇴", description = "현재 로그인된 사용자(승객/기사/관리자)의 계정을 삭제합니다")
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteMyAccount(
            @Parameter(description = "사용자 UUID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @RequestHeader("X-User-UUID") String userUuidHeader) {
        
        log.debug("회원 탈퇴 요청: userUuid={}", userUuidHeader);
        
        UUID userUuid = UUID.fromString(userUuidHeader);
        deleteUserUseCase.execute(userUuid);
        
        log.debug("회원 탈퇴 완료: userUuid={}", userUuid);
        
        return ResponseEntity.ok(ApiResponse.success(null, "회원 탈퇴가 완료되었습니다."));
    }
}