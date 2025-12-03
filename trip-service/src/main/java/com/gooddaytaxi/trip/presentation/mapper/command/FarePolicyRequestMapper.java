package com.gooddaytaxi.trip.presentation.mapper.command;

import com.gooddaytaxi.trip.application.command.FarePolicyCreateCommand;
import com.gooddaytaxi.trip.domain.model.enums.PolicyType;
import com.gooddaytaxi.trip.presentation.dto.request.CreateFarePolicyRequest;
import org.springframework.stereotype.Component;

@Component
public class FarePolicyRequestMapper {

    /**
     * HTTP Request DTO와 생성자 정보를 받아 Command 객체로 변환합니다.
     */
    public FarePolicyCreateCommand toCommand(CreateFarePolicyRequest request) {

        PolicyType policyType = PolicyType.from(request.policyType());
        return new FarePolicyCreateCommand(
                // Record의 필드 접근자 메서드 사용
                policyType,
                request.baseDistance(),
                request.baseFare(),
                request.distRateKm(),
                request.timeRate()
        );
    }

}
