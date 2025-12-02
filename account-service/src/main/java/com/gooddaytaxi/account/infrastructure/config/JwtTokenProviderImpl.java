package com.gooddaytaxi.account.infrastructure.config;

import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.service.JwtTokenProvider;
import com.gooddaytaxi.common.core.exception.BusinessException;
import com.gooddaytaxi.common.core.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * JWT 토큰 생성 구현체
 * 게이트웨이와 동일한 비밀키와 알고리즘을 사용하여 토큰 생성
 */
@Slf4j
@Component
public class JwtTokenProviderImpl implements JwtTokenProvider {
    
    private final String secretKey;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    
    public JwtTokenProviderImpl(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token-validity-in-seconds:3600}") long accessTokenValidityInSeconds,
            @Value("${jwt.refresh-token-validity-in-seconds:604800}") long refreshTokenValidityInSeconds) {
        this.secretKey = secretKey;
        this.accessTokenValidityInMilliseconds = accessTokenValidityInSeconds * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInSeconds * 1000;
    }
    
    @Override
    public String generateAccessToken(User user) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidityInMilliseconds);
        
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        
        String token = Jwts.builder()
                .setSubject(user.getUserUuid().toString())
                .claim("userId", user.getUserUuid().toString())
                .claim("role", user.getRole().name())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
        
        log.debug("JWT 액세스 토큰 생성 완료: userId={}, role={}", user.getUserUuid(), user.getRole());
        return token;
    }
    
    @Override
    public String generateRefreshToken(User user) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidityInMilliseconds);
        
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        
        String token = Jwts.builder()
                .setSubject(user.getUserUuid().toString())
                .claim("userId", user.getUserUuid().toString())
                .claim("tokenType", "refresh")
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
        
        log.debug("JWT 리프레시 토큰 생성 완료: userId={}", user.getUserUuid());
        return token;
    }
    
    @Override
    public Claims parseToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
            
            return Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody();
                    
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰: {}", e.getMessage());
            throw new BusinessException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            log.warn("잘못된 JWT 토큰: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.warn("JWT 서명 불일치: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }
    
    @Override
    public boolean isValidRefreshToken(String token) {
        try {
            Claims claims = parseToken(token);
            
            // 토큰 타입 검증 - refresh 토큰인지 확인
            String tokenType = claims.get("tokenType", String.class);
            if (!"refresh".equals(tokenType)) {
                log.warn("리프레시 토큰이 아닌 토큰으로 재발급 시도");
                return false;
            }
            
            return true;
        } catch (BusinessException e) {
            return false;
        }
    }
}
