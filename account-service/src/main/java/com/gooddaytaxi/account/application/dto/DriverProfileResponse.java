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
public class DriverProfileResponse {
    
    private UUID driverId;
    private String name;
    
    @JsonProperty("vehicle_number")
    private String vehicleNumber;
    
    @JsonProperty("vehicle_type") 
    private String vehicleType;
    
    @JsonProperty("vehicle_color")
    private String vehicleColor;
    
    @JsonProperty("online_status")
    private String onlineStatus;
}