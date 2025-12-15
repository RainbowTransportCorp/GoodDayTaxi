package com.gooddaytaxi.support.adapter.out.internal.account.dto;

/**
 * 차량 정보 조회 DTO
 */
public record VehicleInfo(
    String vehicleType,
    String vehicleNumber,
    String vehicleColor
){}