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
@Schema(description = "관리자 배차 상세 조회 응답")
public class AdminDispatchDetailResponseDto {

    @Schema(description = "배차 ID")
    private final UUID dispatchId;

    @Schema(description = "승객 ID")
    private final UUID passengerId;

    @Schema(description = "기사 ID")
    private final UUID driverId;

    @Schema(description = "배차 상태", example = "ASSIGNED")
    private final DispatchStatus status;

    @Schema(description = "출발지 주소")
    private final String pickupAddress;

    @Schema(description = "도착지 주소")
    private final String destinationAddress;

    @Schema(description = "재배차 시도 횟수")
    private final int reassignCount;

    @Schema(description = "기사 배정 시각")
    private final LocalDateTime assignedAt;

    @Schema(description = "기사 수락 시각")
    private final LocalDateTime acceptedAt;

    @Schema(description = "Timeout 처리 시각")
    private final LocalDateTime timeoutAt;
}
