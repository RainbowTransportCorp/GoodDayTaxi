package com.gooddaytaxi.account.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * Dispatch 서비스용 기사 정보 응답 DTO
 * 
 * 배차 시스템에서 기사 유효성 검증 및 정보 조회용
 */
@Getter
@Builder
public class DispatchDriverInfoResponse {
    
    /**
     * 기사 ID (UUID)
     */
    private final UUID driverId;
    
    /**
     * 기사명
     */
    private final String name;
    
    /**
     * 연락처
     */
    private final String phoneNumber;
    
    /**
     * 사용자 상태 (ACTIVE, INACTIVE, SUSPENDED)
     */
    private final String status;
    
    /**
     * 온라인 상태 (ONLINE, OFFLINE, BUSY)
     */
    private final String onlineStatus;
    
    /**
     * 차량 정보
     */
    private final VehicleInfo vehicleInfo;
    
    /**
     * 차량 정보 내부 클래스
     */
    @Getter
    @Builder
    public static class VehicleInfo {
        
        /**
         * 차종
         */
        private final String vehicleType;
        
        /**
         * 차량번호
         */
        private final String vehicleNumber;
        
        /**
         * 차량색상
         */
        private final String vehicleColor;
    }
}