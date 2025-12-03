package com.gooddaytaxi.account.adapter.web;

import com.gooddaytaxi.account.application.dto.UpdateUserProfileCommand;
import com.gooddaytaxi.account.application.dto.UpdateUserProfileResponse;
import com.gooddaytaxi.account.application.dto.UserProfileResponse;
import com.gooddaytaxi.account.application.usecase.DeleteUserUseCase;
import com.gooddaytaxi.account.application.usecase.GetUserProfileUseCase;
import com.gooddaytaxi.account.application.usecase.UpdateUserProfileUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final GetUserProfileUseCase getUserProfileUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            @RequestHeader("X-User-UUID") String userUuidHeader) {
        
        log.debug("내 정보 조회 요청: userUuid={}", userUuidHeader);
        
        UUID userUuid = UUID.fromString(userUuidHeader);
        UserProfileResponse response = getUserProfileUseCase.execute(userUuid);
        
        log.debug("내 정보 조회 완료: userUuid={}", userUuid);
        
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/me")
    public ResponseEntity<UpdateUserProfileResponse> updateMyProfile(
            @RequestHeader("X-User-UUID") String userUuidHeader,
            @Valid @RequestBody UpdateUserProfileCommand command) {
        
        log.debug("내 정보 수정 요청: userUuid={}, name={}", userUuidHeader, command.getName());
        
        UUID userUuid = UUID.fromString(userUuidHeader);
        UpdateUserProfileResponse response = updateUserProfileUseCase.execute(userUuid, command);
        
        log.debug("내 정보 수정 완료: userUuid={}", userUuid);
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(
            @RequestHeader("X-User-UUID") String userUuidHeader) {
        
        log.debug("회원 탈퇴 요청: userUuid={}", userUuidHeader);
        
        UUID userUuid = UUID.fromString(userUuidHeader);
        deleteUserUseCase.execute(userUuid);
        
        log.debug("회원 탈퇴 완료: userUuid={}", userUuid);
        
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}