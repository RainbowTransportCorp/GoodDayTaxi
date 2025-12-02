package com.gooddaytaxi.account.application.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UpdateUserProfileResponse {
    
    private String message;
    private UUID userUuid;
}