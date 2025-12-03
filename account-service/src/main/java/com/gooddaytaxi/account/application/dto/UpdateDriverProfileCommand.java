package com.gooddaytaxi.account.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class UpdateDriverProfileCommand {
    
    @NotBlank(message = "차량번호는 필수입니다")
    @Pattern(regexp = "^[0-9]{2,3}[가-힣][0-9]{4}$", message = "올바른 차량번호 형식이 아닙니다")
    @JsonProperty("vehicle_number")
    private String vehicleNumber;
    
    @NotBlank(message = "차량 유형은 필수입니다")
    @Size(min = 2, max = 30, message = "차량 유형은 2자 이상 30자 이하여야 합니다")
    @JsonProperty("vehicle_type")
    private String vehicleType;
    
    @NotBlank(message = "차량 색상은 필수입니다")
    @Size(min = 2, max = 20, message = "차량 색상은 2자 이상 20자 이하여야 합니다")
    @JsonProperty("vehicle_color")
    private String vehicleColor;
}