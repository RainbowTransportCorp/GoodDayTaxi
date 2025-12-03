package com.gooddaytaxi.account.application.mapper;

import com.gooddaytaxi.account.application.dto.UpdateDriverStatusResponse;
import com.gooddaytaxi.account.domain.model.DriverProfile;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class UpdateDriverStatusMapper {
    
    public UpdateDriverStatusResponse toResponse(DriverProfile driverProfile) {
        return UpdateDriverStatusResponse.builder()
                .driverId(driverProfile.getUserId())
                .onlineStatus(driverProfile.getOnlineStatus())
                .message("상태 변경 완료")
                .build();
    }
}