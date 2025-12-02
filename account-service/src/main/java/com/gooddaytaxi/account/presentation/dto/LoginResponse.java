package com.gooddaytaxi.account.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 로그인 성공 응답 DTO
 * JWT 토큰과 기본 사용자 정보를 클라이언트에게 전달
 */
@Getter
@AllArgsConstructor
public class LoginResponse {
    
    private final String accessToken;
    private final String refreshToken;
    private final String userUuid;
    private final String role;
    
    public static LoginResponse of(String accessToken, String refreshToken, String userUuid, String role) {
        return new LoginResponse(accessToken, refreshToken, userUuid, role);
    }
}
