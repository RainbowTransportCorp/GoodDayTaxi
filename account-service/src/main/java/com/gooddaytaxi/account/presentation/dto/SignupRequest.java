package com.gooddaytaxi.account.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddaytaxi.account.domain.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원가입 요청 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SignupRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "전화번호는 필수입니다.")
    @JsonProperty("phone_number")
    private String phoneNumber;
    
    @JsonProperty("slack_id")
    private String slackId;

    @NotNull(message = "역할은 필수입니다.")
    private UserRole role;

    // 기사 전용 필드 (DRIVER일 때만 필수)
    @JsonProperty("vehicle_number")
    private String vehicleNumber;

    @JsonProperty("vehicle_type")
    private String vehicleType;

    @JsonProperty("vehicle_color")
    private String vehicleColor;
}