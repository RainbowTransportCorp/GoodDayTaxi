package com.gooddaytaxi.dispatch.presentation.external.dto.request;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "배차(콜) 생성 요청")
public class DispatchCreateRequestDto {

    @Schema(description = "출발지 주소", example = "서울시 강남구 역삼동")
    @NotBlank(message = "출발지 주소는 필수입니다.")
    private String pickupAddress;

    @Schema(description = "도착지 주소", example = "서울시 송파구 잠실동")
    @NotBlank(message = "도착지 주소는 필수입니다.")
    private String destinationAddress;
}
