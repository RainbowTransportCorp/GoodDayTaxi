package com.gooddaytaxi.account.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteUserResponse {
    
    @JsonProperty("userId")
    private UUID userId;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("deleted_at")
    private LocalDateTime deletedAt;
    
    @JsonProperty("message")
    private String message;
}