package com.gooddaytaxi.account.adapter.web;

import com.gooddaytaxi.account.application.dto.UserProfileResponse;
import com.gooddaytaxi.account.application.usecase.GetUserProfileUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final GetUserProfileUseCase getUserProfileUseCase;
    
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            @RequestHeader("X-User-UUID") String userUuidHeader) {
        
        log.debug("내 정보 조회 요청: userUuid={}", userUuidHeader);
        
        UUID userUuid = UUID.fromString(userUuidHeader);
        UserProfileResponse response = getUserProfileUseCase.execute(userUuid);
        
        log.debug("내 정보 조회 완료: userUuid={}", userUuid);
        
        return ResponseEntity.ok(response);
    }
}