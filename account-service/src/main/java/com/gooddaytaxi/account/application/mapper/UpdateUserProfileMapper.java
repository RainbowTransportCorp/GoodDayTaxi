package com.gooddaytaxi.account.application.mapper;

import com.gooddaytaxi.account.application.dto.UpdateUserProfileResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UpdateUserProfileMapper {
    
    public UpdateUserProfileResponse toResponse(UUID userUuid) {
        return UpdateUserProfileResponse.builder()
                .message("정보 수정 완료")
                .userUuid(userUuid)
                .build();
    }
}