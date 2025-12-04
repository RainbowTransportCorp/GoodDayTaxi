package com.gooddaytaxi.account.application.mapper;

import com.gooddaytaxi.account.application.dto.AdminUserDetailResponse;
import com.gooddaytaxi.account.domain.model.User;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class AdminUserDetailMapper {
    
    public AdminUserDetailResponse toResponse(User user) {
        return AdminUserDetailResponse.builder()
                .userId(user.getUserUuid())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}