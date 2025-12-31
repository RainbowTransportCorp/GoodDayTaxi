package com.gooddaytaxi.account.application.usecase;

import com.gooddaytaxi.account.application.dto.LoginResult;
import com.gooddaytaxi.account.application.dto.UserLoginCommand;
import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.repository.UserRepository;
import com.gooddaytaxi.account.domain.service.JwtTokenProvider;
import com.gooddaytaxi.account.domain.service.PasswordEncoder;
import com.gooddaytaxi.account.domain.exception.AccountBusinessException;
import com.gooddaytaxi.account.domain.exception.AccountErrorCode;
import com.gooddaytaxi.common.core.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 로그인 기능을 수행하는 UseCase
 * SRP: 로그인 처리에만 집중하여 단일 책임을 가짐
 * DIP: UserRepository, JwtTokenProvider 추상화에 의존하여 구현체의 변경에 영향받지 않음
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginUserUseCase {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    /**
     * 사용자 로그인 처리
     * 
     * @param command 로그인 요청 정보
     * @return JWT 토큰과 사용자 정보
     * @throws BusinessException 로그인 실패 시
     */
    public LoginResult execute(UserLoginCommand command) {
        log.debug("사용자 로그인 시도: email={}", command.getEmail());
        
        User user = userRepository.findByEmailAndDeletedAtIsNull(command.getEmail())
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 이메일로 로그인 시도: {}", command.getEmail());
                    return new AccountBusinessException(AccountErrorCode.INVALID_CREDENTIALS);
                });
        
        if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
            log.warn("비밀번호 불일치로 로그인 실패: email={}", command.getEmail());
            throw new AccountBusinessException(AccountErrorCode.INVALID_CREDENTIALS);
        }
        
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        
        log.info("로그인 성공: userUuid={}, email={}, role={}", user.getUserUuid(), command.getEmail(), user.getRole());
        return new LoginResult(accessToken, refreshToken, user.getUserUuid().toString(), user.getRole().name(), user.getEmail());
    }
}
