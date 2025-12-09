package com.gooddaytaxi.account.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * 배차 가능한 기사 목록 응답 DTO
 * 
 * 지역별 대기중인 기사들의 UUID 목록 제공
 */
@Getter
@Builder
public class AvailableDriversResponse {
    
    /**
     * 배차 가능한 기사 UUID 목록
     */
    private final List<UUID> driverIds;
    
    /**
     * 조회 지역 정보
     */
    private final String region;
    
    /**
     * 총 기사 수
     */
    private final int totalCount;
}