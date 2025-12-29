package com.gooddaytaxi.account.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddaytaxi.account.domain.exception.AccountBusinessException;
import com.gooddaytaxi.account.domain.exception.AccountErrorCode;
import com.gooddaytaxi.account.domain.model.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "회원가입 요청", example = """
    {
      "email": "driver1@goodday.com",
      "password": "Password123!",
      "name": "김기사",
      "phone_number": "010-1234-5678",
      "slack_id": "U01234ABCDE",
      "role": "DRIVER",
      "vehicle_number": "12가3456",
      "vehicle_type": "소나타",
      "vehicle_color": "흰색"
    }
    """)
public class SignupRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Schema(description = "이메일 주소", example = "driver1@goodday.com")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Schema(description = "비밀번호 (8자 이상, 영문 대소문자, 숫자, 특수문자 포함)", example = "Password123!")
    private String password;

    @NotBlank(message = "이름은 필수입니다.")
    @Schema(description = "사용자 이름", example = "김기사")
    private String name;

    @NotBlank(message = "전화번호는 필수입니다.")
    @JsonProperty("phone_number")
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phoneNumber;
    
    @JsonProperty("slack_id")
    @Schema(description = "슬랙 사용자 ID (승객/기사/MASTER_ADMIN 필수, ADMIN 불필요)", example = "U01234ABCDE")
    private String slackId;

    @NotNull(message = "역할은 필수입니다.")
    @Schema(description = "사용자 역할 (PASSENGER/DRIVER/ADMIN/MASTER_ADMIN)", example = "DRIVER")
    private String role;

    @JsonProperty("vehicle_number")
    @Schema(description = "차량번호 (기사 전용)", example = "12가3456")
    private String vehicleNumber;

    @JsonProperty("vehicle_type")
    @Schema(description = "차량 종류 (기사 전용)", example = "소나타")
    private String vehicleType;

    @JsonProperty("vehicle_color")
    @Schema(description = "차량 색상 (기사 전용)", example = "흰색")
    private String vehicleColor;

    public UserRole toUserRoleOrThrow() {
        try {
            return UserRole.valueOf(this.role.toUpperCase());
        } catch (Exception e) {
            throw new AccountBusinessException(AccountErrorCode.INVALID_INPUT_VALUE);
        }
    }

}
