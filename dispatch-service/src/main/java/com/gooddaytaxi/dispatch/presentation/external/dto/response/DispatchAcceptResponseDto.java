package com.gooddaytaxi.dispatch.presentation.external.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "배차 수락 응답")
public class DispatchAcceptResponseDto {

    @Schema(description = "배차 ID")
    private final UUID dispatchId;

    @Schema(description = "배차를 수락한 기사 ID")
    private final UUID driverId;

    @Schema(description = "배차 상태", example = "ACCEPTED")
    private final String dispatchStatus;

    @Schema(description = "배차 수락 시각")
    private final LocalDateTime acceptedAt;
}
