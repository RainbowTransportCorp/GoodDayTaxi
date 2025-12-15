package com.gooddaytaxi.support.adapter.out.internal.account.dto;

import java.util.UUID;

/**
 * 기사 정보 조회 DTO
 */
public record DriverProfile(
        UUID driverId,
        String name,
        String phoneNumber,
        String status,
        String onlineStatus,
        VehicleInfo vehicleInfo
){}
