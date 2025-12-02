package com.gooddaytaxi.account.domain.service;

import com.gooddaytaxi.account.domain.model.User;

/**
 * JWT 토큰 생성 인터페이스
 * DIP: 인프라 계층의 JWT 구현체에 의존하지 않도록 추상화
 */
public interface JwtTokenProvider {
    
    /**
     * 사용자 정보로 JWT 액세스 토큰 생성
     * 
     * @param user 사용자 엔티티
     * @return JWT 액세스 토큰
     */
    String generateAccessToken(User user);
    
    /**
     * 사용자 정보로 JWT 리프레시 토큰 생성
     * 
     * @param user 사용자 엔티티
     * @return JWT 리프레시 토큰
     */
    String generateRefreshToken(User user);
}
