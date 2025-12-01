package com.gooddaytaxi.account.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원가입 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SignupResponse {

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("message")
    private String message;

    public static SignupResponse of(String userId) {
        return SignupResponse.builder()
                .userId(userId)
                .message("회원가입 완료")
                .build();
    }
}