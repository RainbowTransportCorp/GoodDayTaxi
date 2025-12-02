package com.gooddaytaxi.account.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 요청 DTO
 * 클라이언트로부터 수신한 로그인 정보를 담는 객체
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @Email(message = "유효한 이메일 형식이 아닙니다")
    @NotBlank(message = "이메일은 필수입니다")
    private String email;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    private String password;
}
