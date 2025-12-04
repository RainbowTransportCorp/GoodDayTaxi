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
public class ChangeUserStatusCommand {
    
    @NotBlank(message = "상태는 필수입니다")
    @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "상태는 ACTIVE 또는 INACTIVE여야 합니다")
    @JsonProperty("status")
    private String status;
}