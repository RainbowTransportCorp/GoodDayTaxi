package com.gooddaytaxi.dispatch.presentation.external.dto.response;

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
@Schema(description = "배차 취소 응답")
public class DispatchCancelResponseDto {

    @Schema(description = "배차 ID")
    private final UUID dispatchId;

    @Schema(description = "배차 상태", example = "CANCELLED")
    private final String dispatchStatus;

    @Schema(description = "배차 취소 시각")
    private final LocalDateTime canceledAt;
}
