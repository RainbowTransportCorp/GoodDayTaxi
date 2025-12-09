package com.gooddaytaxi.account.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "기사 프로필 수정 요청", example = """
    {
      "vehicle_number": "15가6789",
      "vehicle_type": "그랜저",
      "vehicle_color": "검은색"
    }
    """)
public class UpdateDriverProfileCommand {
    
    @NotBlank(message = "차량번호는 필수입니다")
    @Pattern(regexp = "^[0-9]{2,3}[가-힣][0-9]{4}$", message = "올바른 차량번호 형식이 아닙니다")
    @JsonProperty("vehicle_number")
    @Schema(description = "변경할 차량번호", example = "15가6789")
    private String vehicleNumber;
    
    @NotBlank(message = "차량 유형은 필수입니다")
    @Size(min = 2, max = 30, message = "차량 유형은 2자 이상 30자 이하여야 합니다")
    @JsonProperty("vehicle_type")
    @Schema(description = "변경할 차량 유형", example = "그랜저")
    private String vehicleType;
    
    @NotBlank(message = "차량 색상은 필수입니다")
    @Size(min = 2, max = 20, message = "차량 색상은 2자 이상 20자 이하여야 합니다")
    @JsonProperty("vehicle_color")
    @Schema(description = "변경할 차량 색상", example = "검은색")
    private String vehicleColor;
}