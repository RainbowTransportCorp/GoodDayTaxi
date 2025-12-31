package com.gooddaytaxi.dispatch.presentation.external.dto.response;

import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/* Swagger */
import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@Getter
@AllArgsConstructor
@Schema(description = "관리자 배차 목록 조회 응답")
public class AdminDispatchListResponseDto {

    @Schema(description = "배차 ID")
    UUID dispatchId;

    @Schema(description = "승객 ID")
    UUID passengerId;

    @Schema(description = "기사 ID")
    UUID driverId;

    @Schema(description = "배차 상태", example = "REQUESTED")
    DispatchStatus status;

    @Schema(description = "재배차 시도 횟수")
    int reassignCount;

    @Schema(description = "콜 요청 시각")
    LocalDateTime requestedAt;

    @Schema(description = "마지막 상태 변경 시각")
    LocalDateTime updatedAt;
}
