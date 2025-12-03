package com.gooddaytaxi.account.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDriverStatusResponse {
    
    @JsonProperty("driverId")
    private UUID driverId;
    
    @JsonProperty("online_status")
    private String onlineStatus;
    
    @JsonProperty("message")
    private String message;
}