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
@Schema(description = "배차 상세 조회 응답")
public class DispatchDetailResponseDto {

    @Schema(description = "배차 ID")
    private final UUID dispatchId;

    @Schema(description = "승객 ID")
    private final UUID passengerId;

    @Schema(description = "기사 ID")
    private final UUID driverId;

    @Schema(description = "출발지 주소")
    private final String pickupAddress;

    @Schema(description = "도착지 주소")
    private final String destinationAddress;

    @Schema(description = "배차 상태", example = "ASSIGNED")
    private final String dispatchStatus;

    @Schema(description = "콜 요청 생성 시각")
    private final LocalDateTime requestCreatedAt;

    @Schema(description = "기사 배정 시각")
    private final LocalDateTime assignedAt;

    @Schema(description = "기사 수락 시각")
    private final LocalDateTime acceptedAt;

    @Schema(description = "콜 취소 시각")
    private final LocalDateTime canceledAt;

    @Schema(description = "Timeout 처리 시각")
    private final LocalDateTime timeoutAt;

    @Schema(description = "엔티티 생성 시각")
    private final LocalDateTime createdAt;

    @Schema(description = "마지막 수정 시각")
    private final LocalDateTime updatedAt;
}
