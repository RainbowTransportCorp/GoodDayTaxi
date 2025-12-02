package com.gooddaytaxi.account.application.usecase;

import com.gooddaytaxi.account.application.dto.RefreshTokenCommand;
import com.gooddaytaxi.account.application.dto.RefreshTokenResult;
import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.repository.UserReadRepository;
import com.gooddaytaxi.account.domain.service.JwtTokenProvider;
import com.gooddaytaxi.common.core.exception.BusinessException;
import com.gooddaytaxi.common.core.exception.ErrorCode;
import io.jsonwebtoken.Claims;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급하는 UseCase
 * SRP: 토큰 재발급 처리에만 집중하여 단일 책임을 가짐
 * DIP: UserReadRepository, JwtTokenProvider 추상화에 의존하여 구현체의 변경에 영향받지 않음
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenUseCase {
    
    private final UserReadRepository userReadRepository;
    private final JwtTokenProvider jwtTokenProvider;
    
    /**
     * 리프레시 토큰을 사용하여 새로운 토큰 재발급
     * 
     * @param command 토큰 재발급 요청 정보
     * @return 새로운 액세스 토큰과 리프레시 토큰
     * @throws BusinessException 토큰이 유효하지 않거나 사용자를 찾을 수 없는 경우
     */
    public RefreshTokenResult execute(RefreshTokenCommand command) {
        log.debug("토큰 재발급 시도");
        
        // 1. 리프레시 토큰 유효성 검증
        if (!jwtTokenProvider.isValidRefreshToken(command.getRefreshToken())) {
            log.warn("유효하지 않은 리프레시 토큰으로 재발급 시도");
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        
        // 2. 토큰에서 사용자 정보 추출
        Claims claims = jwtTokenProvider.parseToken(command.getRefreshToken());
        String userId = claims.get("userId", String.class);
        
        if (userId == null || userId.isBlank()) {
            log.warn("리프레시 토큰에 사용자 정보가 없음");
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        
        // 3. 사용자 존재 여부 및 활성 상태 확인
        UUID userUuid = UUID.fromString(userId);
        User user = userReadRepository.findById(userUuid)
                .orElseThrow(() -> {
                    log.warn("리프레시 토큰에 포함된 사용자가 존재하지 않음: userId={}", userId);
                    return new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
                });
        
        if (user.isDeleted() || !user.isActive()) {
            log.warn("비활성 또는 삭제된 사용자로 토큰 재발급 시도: userId={}", userId);
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        
        // 4. 새로운 토큰 발급 (Refresh Token Rotation)
        String newAccessToken = jwtTokenProvider.generateAccessToken(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);
        
        log.info("토큰 재발급 성공: userId={}", userId);
        return new RefreshTokenResult(newAccessToken, newRefreshToken);
    }
}
