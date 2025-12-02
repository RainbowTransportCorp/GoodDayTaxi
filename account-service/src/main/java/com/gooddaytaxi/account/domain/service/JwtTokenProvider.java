package com.gooddaytaxi.account.domain.service;

import com.gooddaytaxi.account.domain.model.User;

import io.jsonwebtoken.Claims;

/**
 * JWT 토큰 생성 및 검증 인터페이스
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
    
    /**
     * JWT 토큰에서 Claims 추출
     * 
     * @param token JWT 토큰
     * @return Claims 정보
     * @throws BusinessException 토큰이 유효하지 않은 경우
     */
    Claims parseToken(String token);
    
    /**
     * 리프레시 토큰 유효성 검증
     * 
     * @param token 리프레시 토큰
     * @return 유효하면 true, 아니면 false
     */
    boolean isValidRefreshToken(String token);
}
