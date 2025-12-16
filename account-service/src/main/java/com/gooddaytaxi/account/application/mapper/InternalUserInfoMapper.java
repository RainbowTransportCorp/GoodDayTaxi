package com.gooddaytaxi.account.application.mapper;

import com.gooddaytaxi.account.application.dto.InternalUserInfoResponse;
import com.gooddaytaxi.account.domain.model.DriverProfile;
import com.gooddaytaxi.account.domain.model.User;
import org.springframework.stereotype.Component;

/**
 * Internal API용 사용자 정보 매퍼
 * 
 * Single Responsibility: User, DriverProfile -> InternalUserInfoResponse 변환만 담당
 * Open/Closed: 새로운 매핑 요구사항이 생기면 메소드 추가
 */
@Component
public class InternalUserInfoMapper {
    
    /**
     * User와 DriverProfile을 InternalUserInfoResponse로 변환
     * 
     * @param user 사용자 엔티티
     * @param driverProfile 기사 프로필 (기사가 아니면 null)
     * @return Internal API 응답 DTO
     */
    public InternalUserInfoResponse toResponse(User user, DriverProfile driverProfile) {
        InternalUserInfoResponse.InternalUserInfoResponseBuilder builder = InternalUserInfoResponse.builder()
                .userId(user.getUserUuid())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .status(user.getStatus())
                .slackUserId(user.getSlackId()); // User 테이블에서 슬랙ID 가져오기
        
        // 기사인 경우 추가 정보 설정
        if (driverProfile != null) {
            builder.onlineStatus(driverProfile.getOnlineStatus());
        }
        
        return builder.build();
    }
}