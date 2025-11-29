package com.gooddaytaxi.dispatch.presentation.external.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchCreateRequestDto {

    @NotNull(message = "승객 ID는 필수입니다.")
    private Long passengerId;

    @NotBlank(message = "출발지 주소는 필수입니다.")
    private String pickupAddress;

    @NotBlank(message = "도착지 주소는 필수입니다.")
    private String destinationAddress;
}