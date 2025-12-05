package com.gooddaytaxi.account.application.dto;

import com.gooddaytaxi.account.domain.model.UserRole;
import com.gooddaytaxi.account.domain.model.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Internal API용 사용자 정보 응답 DTO
 * support-service의 슬랙 알림 등에 활용
 */
@Data
@Builder
public class InternalUserInfoResponse {
    
    /**
     * 사용자 고유 ID
     */
    private UUID userId;
    
    /**
     * 사용자 이름
     */
    private String name;
    
    /**
     * 사용자 이메일
     */
    private String email;
    
    /**
     * 사용자 전화번호
     */
    private String phoneNumber;
    
    /**
     * 사용자 역할 (DRIVER, PASSENGER, ADMIN)
     */
    private UserRole role;
    
    /**
     * 사용자 상태 (ACTIVE, INACTIVE, SUSPENDED 등)
     */
    private UserStatus status;
    
    /**
     * 슬랙 사용자 ID (기사인 경우만)
     * 예: "U1234567890"
     */
    private String slackUserId;
    
    /**
     * 온라인 상태 (기사인 경우만)
     * 예: "ONLINE", "OFFLINE"
     */
    private String onlineStatus;
}