package com.gooddaytaxi.account.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 토큰 재발급 성공 응답 DTO
 * 새로운 액세스 토큰과 리프레시 토큰을 클라이언트에게 전달
 */
@Getter
@AllArgsConstructor
public class RefreshTokenResponse {
    
    private final String accessToken;
    private final String refreshToken;
    
    public static RefreshTokenResponse of(String accessToken, String refreshToken) {
        return new RefreshTokenResponse(accessToken, refreshToken);
    }
}
