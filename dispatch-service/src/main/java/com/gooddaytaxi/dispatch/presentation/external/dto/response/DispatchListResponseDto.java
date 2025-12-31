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
@Schema(description = "배차 목록 조회 응답")
public class DispatchListResponseDto {

    @Schema(description = "배차 ID")
    private final UUID dispatchId;

    @Schema(description = "출발지 주소")
    private final String pickupAddress;

    @Schema(description = "도착지 주소")
    private final String destinationAddress;

    @Schema(description = "배차 상태", example = "REQUESTED")
    private final String dispatchStatus;

    @Schema(description = "배정된 기사 ID (미배정 시 null)")
    private final UUID driverId;

    @Schema(description = "콜 요청 생성 시각")
    private final LocalDateTime requestCreatedAt;
}
