package com.gooddaytaxi.account.domain.service;

/**
 * 패스워드 인코딩 인터페이스
 */
public interface PasswordEncoder {
    
    /**
     * 평문 비밀번호를 암호화
     *
     * @param rawPassword 평문 비밀번호
     * @return 암호화된 비밀번호
     */
    String encode(String rawPassword);
    
    /**
     * 평문 비밀번호와 암호화된 비밀번호 일치 여부 확인
     *
     * @param rawPassword 평문 비밀번호
     * @param encodedPassword 암호화된 비밀번호
     * @return 일치하면 true, 아니면 false
     */
    boolean matches(String rawPassword, String encodedPassword);
}