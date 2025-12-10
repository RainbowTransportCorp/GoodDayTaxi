package com.gooddaytaxi.dispatch.infrastructure.client.account.dto;

import java.util.List;
import java.util.UUID;

public record DriverInfo(
        List<UUID> driverIds, //기사 리스트
        String region, //조회 지역 정보
        int totalCount //총 기사 수
) {}
