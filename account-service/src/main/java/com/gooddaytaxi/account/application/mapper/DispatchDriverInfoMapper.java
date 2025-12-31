package com.gooddaytaxi.account.application.mapper;

import com.gooddaytaxi.account.application.dto.DispatchDriverInfoResponse;
import com.gooddaytaxi.account.domain.model.DriverProfile;
import com.gooddaytaxi.account.domain.model.User;
import org.springframework.stereotype.Component;

/**
 * Dispatch 서비스용 기사 정보 매퍼
 * 
 * Domain 객체를 Dispatch API 응답 DTO로 변환
 */
@Component
public class DispatchDriverInfoMapper {
    
    /**
     * User와 DriverProfile을 DispatchDriverInfoResponse로 변환
     * 
     * @param user 사용자 정보
     * @param driverProfile 기사 프로필 정보
     * @return Dispatch용 기사 정보 응답
     */
    public DispatchDriverInfoResponse toResponse(User user, DriverProfile driverProfile) {
        return DispatchDriverInfoResponse.builder()
                .driverId(user.getUserUuid())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus().name())
                .onlineStatus(driverProfile != null ? driverProfile.getOnlineStatus() : "OFFLINE")
                .vehicleInfo(mapVehicleInfo(driverProfile))
                .build();
    }
    
    /**
     * DriverProfile을 VehicleInfo로 변환
     * 
     * @param driverProfile 기사 프로필
     * @return 차량 정보, 프로필이 없으면 null
     */
    private DispatchDriverInfoResponse.VehicleInfo mapVehicleInfo(DriverProfile driverProfile) {
        if (driverProfile == null) {
            return null;
        }
        
        return DispatchDriverInfoResponse.VehicleInfo.builder()
                .vehicleType(driverProfile.getVehicleType())
                .vehicleNumber(driverProfile.getVehicleNumber())
                .vehicleColor(driverProfile.getVehicleColor())
                .build();
    }
}