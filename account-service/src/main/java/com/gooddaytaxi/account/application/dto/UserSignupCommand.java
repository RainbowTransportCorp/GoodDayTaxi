package com.gooddaytaxi.account.application.dto;

import com.gooddaytaxi.account.domain.model.UserRole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원가입 명령 객체 - UseCase 입력 데이터
 */
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserSignupCommand {
    private String email;
    private String password;
    private String name;
    private String phoneNumber;
    private UserRole role;
    private String vehicleNumber;
    private String vehicleType;
    private String vehicleColor;
}