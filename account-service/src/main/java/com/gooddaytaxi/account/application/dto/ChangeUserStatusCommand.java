package com.gooddaytaxi.account.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "사용자 상태 변경 요청", example = """
    {
      "status": "ACTIVE"
    }
    """)
public class ChangeUserStatusCommand {
    
    @NotBlank(message = "상태는 필수입니다")
    @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "상태는 ACTIVE 또는 INACTIVE여야 합니다")
    @JsonProperty("status")
    @Schema(description = "변경할 사용자 상태", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
    private String status;
}