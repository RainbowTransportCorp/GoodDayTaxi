package com.gooddaytaxi.account.infrastructure.security;

import com.gooddaytaxi.account.domain.model.UserRole;
import com.gooddaytaxi.common.core.exception.BusinessException;
import com.gooddaytaxi.common.core.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * 게이트웨이에서 전달된 사용자 인증 정보를 추출하는 유틸리티
 * X-User-UUID, X-User-Role 헤더로부터 사용자 정보를 가져옴
 */
@Slf4j
@Component
public class AuthenticationContext {
    
    private static final String USER_UUID_HEADER = "X-User-UUID";
    private static final String USER_ROLE_HEADER = "X-User-Role";
    
    /**
     * 현재 요청의 인증된 사용자 정보를 가져옴
     * 
     * @return 인증된 사용자 정보 (Optional)
     */
    public Optional<AuthenticatedUser> getCurrentUser() {
        try {
            HttpServletRequest request = getCurrentRequest();
            if (request == null) {
                log.debug("현재 HTTP 요청이 없어 사용자 정보를 가져올 수 없음");
                return Optional.empty();
            }
            
            String userUuid = request.getHeader(USER_UUID_HEADER);
            String userRoleStr = request.getHeader(USER_ROLE_HEADER);
            
            if (!StringUtils.hasText(userUuid)) {
                log.debug("{} 헤더가 비어있음", USER_UUID_HEADER);
                return Optional.empty();
            }
            
            UserRole userRole = null;
            if (StringUtils.hasText(userRoleStr)) {
                try {
                    userRole = UserRole.valueOf(userRoleStr);
                } catch (IllegalArgumentException e) {
                    log.warn("잘못된 사용자 역할: {}", userRoleStr, e);
                    return Optional.empty();
                }
            }
            
            return Optional.of(new AuthenticatedUser(userUuid, userRole));
            
        } catch (Exception e) {
            log.warn("사용자 인증 정보 추출 중 오류 발생", e);
            return Optional.empty();
        }
    }
    
    /**
     * 현재 요청에서 인증된 사용자 정보를 반드시 가져옴
     * 인증되지 않은 요청일 경우 예외를 발생
     * 
     * @return 인증된 사용자 정보
     * @throws BusinessException 인증 정보가 없을 경우
     */
    public AuthenticatedUser requireCurrentUser() {
        return getCurrentUser()
                .orElseThrow(() -> {
                    log.warn("인증되지 않은 요청에서 requireCurrentUser 호출");
                    return new BusinessException(ErrorCode.AUTH_TOKEN_MISSING);
                });
    }
    
    /**
     * 현재 HTTP 요청을 가져옴
     */
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
