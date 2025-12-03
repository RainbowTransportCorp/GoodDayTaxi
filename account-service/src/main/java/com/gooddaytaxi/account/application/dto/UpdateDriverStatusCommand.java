package com.gooddaytaxi.account.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDriverStatusCommand {
    
    @NotBlank(message = "온라인 상태는 필수입니다")
    @Pattern(regexp = "^(ONLINE|OFFLINE)$", message = "온라인 상태는 ONLINE 또는 OFFLINE이어야 합니다")
    @JsonProperty("online_status")
    private String onlineStatus;
}