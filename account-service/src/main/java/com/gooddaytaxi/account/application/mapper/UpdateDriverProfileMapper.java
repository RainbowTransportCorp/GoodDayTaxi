package com.gooddaytaxi.account.application.mapper;

import com.gooddaytaxi.account.application.dto.UpdateDriverProfileResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UpdateDriverProfileMapper {
    
    public UpdateDriverProfileResponse toResponse(UUID driverId) {
        return UpdateDriverProfileResponse.builder()
                .message("기사 프로필 수정 완료")
                .driverId(driverId)
                .build();
    }
}