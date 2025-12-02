package com.gooddaytaxi.account.infrastructure.security;

import com.gooddaytaxi.account.domain.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게이트웨이에서 전달된 인증된 사용자 정보
 * 헤더에서 추출한 사용자 UUID와 Role 정보를 담는 객체
 */
@Getter
@AllArgsConstructor
public class AuthenticatedUser {
    
    private final String userUuid;
    private final UserRole role;
}
