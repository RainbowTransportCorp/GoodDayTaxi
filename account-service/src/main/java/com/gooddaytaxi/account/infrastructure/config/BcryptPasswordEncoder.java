package com.gooddaytaxi.account.infrastructure.config;

import com.gooddaytaxi.account.domain.service.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * BCrypt 기반 패스워드 인코더 구현체
 */
@Component
@RequiredArgsConstructor
public class BcryptPasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 평문 비밀번호를 BCrypt로 암호화
     *
     * @param rawPassword 평문 비밀번호
     * @return BCrypt로 암호화된 비밀번호
     */
    @Override
    public String encode(String rawPassword) {
        return bCryptPasswordEncoder.encode(rawPassword);
    }

    /**
     * 평문 비밀번호와 암호화된 비밀번호 일치 여부 확인
     *
     * @param rawPassword 평문 비밀번호
     * @param encodedPassword BCrypt로 암호화된 비밀번호
     * @return 일치하면 true, 아니면 false
     */
    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
}