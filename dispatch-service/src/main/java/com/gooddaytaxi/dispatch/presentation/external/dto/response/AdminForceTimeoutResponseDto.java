package com.gooddaytaxi.dispatch.presentation.external.dto.response;

import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/* Swagger */
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "관리자 강제 Timeout 처리 응답")
public class AdminForceTimeoutResponseDto {

    @Schema(description = "배차 ID")
    private UUID dispatchId;

    @Schema(description = "배차 상태", example = "TIMEOUT")
    private DispatchStatus status;

    @Schema(description = "강제 Timeout 처리 시각")
    private LocalDateTime timeoutAt;
}
