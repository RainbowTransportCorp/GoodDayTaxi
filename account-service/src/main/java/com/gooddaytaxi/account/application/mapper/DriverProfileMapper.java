package com.gooddaytaxi.account.application.mapper;

import com.gooddaytaxi.account.application.dto.DriverProfileResponse;
import com.gooddaytaxi.account.domain.model.DriverProfile;
import com.gooddaytaxi.account.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class DriverProfileMapper {
    
    public DriverProfileResponse toResponse(User user, DriverProfile driverProfile) {
        if (user == null || driverProfile == null) {
            return null;
        }
        
        return DriverProfileResponse.builder()
                .driverId(user.getUserUuid())
                .name(user.getName())
                .vehicleNumber(driverProfile.getVehicleNumber())
                .vehicleType(driverProfile.getVehicleType())
                .vehicleColor(driverProfile.getVehicleColor())
                .onlineStatus(driverProfile.getOnlineStatus())
                .build();
    }
}