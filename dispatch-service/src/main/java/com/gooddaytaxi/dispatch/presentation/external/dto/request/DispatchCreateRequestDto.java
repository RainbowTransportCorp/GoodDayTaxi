package com.gooddaytaxi.dispatch.presentation.external.dto.request;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DispatchCreateRequestDto {

    @NotBlank(message = "출발지 주소는 필수입니다.")
    private String pickupAddress;

    @NotBlank(message = "도착지 주소는 필수입니다.")
    private String destinationAddress;
}