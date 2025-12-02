package com.gooddaytaxi.account.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 토큰 재발급 요청을 담는 Command 객체
 * 응용 계층에서 도메인 계층으로 데이터를 전달하는 역할
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenCommand {
    
    private String refreshToken;
}
