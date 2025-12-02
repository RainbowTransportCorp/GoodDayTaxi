package com.gooddaytaxi.account.application.dto;

import com.gooddaytaxi.account.domain.model.UserRole;
import com.gooddaytaxi.account.domain.model.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserProfileResponse {
    
    private UUID userUuid;
    private String name;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private UserStatus status;
}