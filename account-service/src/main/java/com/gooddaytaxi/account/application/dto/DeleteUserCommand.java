package com.gooddaytaxi.account.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 삭제 요청", example = """
    {
      "reason": "서비스 이용 규칙 위반으로 인한 계정 삭제"
    }
    """)
public class DeleteUserCommand {
    
    @NotBlank(message = "삭제 사유는 필수입니다")
    @Size(min = 5, max = 200, message = "삭제 사유는 5자 이상 200자 이하여야 합니다")
    @JsonProperty("reason")
    @Schema(description = "사용자 삭제 사유", example = "서비스 이용 규칙 위반으로 인한 계정 삭제")
    private String reason;
}