package com.gooddaytaxi.account.application.mapper;

import com.gooddaytaxi.account.application.dto.UserProfileResponse;
import com.gooddaytaxi.account.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMapper {
    
    public UserProfileResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        
        return UserProfileResponse.builder()
                .userUuid(user.getUserUuid())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }
}