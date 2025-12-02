package com.gooddaytaxi.account.infrastructure.config;

import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.service.JwtTokenProvider;
import io.jsonwebtoken.Jwts;
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
                .setSubject(user.getUserId())
                .claim("userId", user.getUserId())
                .claim("role", user.getRole().name())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
        
        log.debug("JWT 액세스 토큰 생성 완료: userId={}, role={}", user.getUserId(), user.getRole());
        return token;
    }
    
    @Override
    public String generateRefreshToken(User user) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidityInMilliseconds);
        
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        
        String token = Jwts.builder()
                .setSubject(user.getUserId())
                .claim("userId", user.getUserId())
                .claim("tokenType", "refresh")
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
        
        log.debug("JWT 리프레시 토큰 생성 완료: userId={}", user.getUserId());
        return token;
    }
}
