package com.gooddaytaxi.trip.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateTripRequest(
        @NotNull(message = "요금 정책 ID는 필수입니다.")
        UUID policyId,

        @NotNull(message = "승객 ID는 필수입니다.")
        UUID passengerId,

        @NotNull(message = "기사 ID는 필수입니다.")
        UUID driverId,

        @NotBlank(message = "픽업 주소는 필수 입력 값입니다.")
        @Size(max = 255, message = "픽업 주소는 최대 255자까지 가능합니다.")
        String pickupAddress,

        @NotBlank(message = "도착지 주소는 필수 입력 값입니다.")
        @Size(max = 255, message = "도착지 주소는 최대 255자까지 가능합니다.")
        String destinationAddress
) {
}
