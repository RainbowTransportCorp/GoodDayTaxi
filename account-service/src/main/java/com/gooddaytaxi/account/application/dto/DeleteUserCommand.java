package com.gooddaytaxi.account.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class DeleteUserCommand {
    
    @NotBlank(message = "삭제 사유는 필수입니다")
    @Size(min = 5, max = 200, message = "삭제 사유는 5자 이상 200자 이하여야 합니다")
    @JsonProperty("reason")
    private String reason;
}