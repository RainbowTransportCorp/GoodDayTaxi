package com.gooddaytaxi.account.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 로그인 처리 결과를 담는 객체
 * UseCase에서 Controller로 데이터를 전달하는 역할
 */
@Getter
@AllArgsConstructor
public class LoginResult {
    
    private final String accessToken;
    private final String refreshToken;
    private final String userUuid;
    private final String role;
}
