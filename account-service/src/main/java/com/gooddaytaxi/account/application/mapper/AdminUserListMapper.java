package com.gooddaytaxi.account.application.mapper;

import com.gooddaytaxi.account.application.dto.AdminUserListResponse;
import com.gooddaytaxi.account.domain.model.User;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class AdminUserListMapper {
    
    public AdminUserListResponse toResponse(User user) {
        return AdminUserListResponse.builder()
                .userUuid(user.getUserUuid())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .build();
    }
    
    public List<AdminUserListResponse> toResponseList(List<User> users) {
        return users.stream()
                .map(this::toResponse)
                .toList();
    }
}