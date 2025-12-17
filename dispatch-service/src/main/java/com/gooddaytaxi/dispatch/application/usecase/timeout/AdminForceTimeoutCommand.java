package com.gooddaytaxi.dispatch.application.usecase.timeout;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class AdminForceTimeoutCommand {

    private UUID adminId;
    @NotBlank
    private String reason; // "장시간 미응답", "시스템 오류" 등 (로그/감사용)
}