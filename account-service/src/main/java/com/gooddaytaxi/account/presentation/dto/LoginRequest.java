package com.gooddaytaxi.account.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "로그인 요청", example = """
    {
      "email": "driver1@goodday.com",
      "password": "password123!"
    }
    """)
public class LoginRequest {
    
    @Email(message = "유효한 이메일 형식이 아닙니다")
    @NotBlank(message = "이메일은 필수입니다")
    @Schema(description = "이메일 주소", example = "driver1@goodday.com")
    private String email;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    @Schema(description = "비밀번호", example = "password123!")
    private String password;
}
