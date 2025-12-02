package com.gooddaytaxi.trip.presentation.mapper.result;

import com.gooddaytaxi.trip.application.result.FarePolicyCreateResult;
import com.gooddaytaxi.trip.domain.model.FarePolicy;
import com.gooddaytaxi.trip.presentation.dto.response.FarePolicyResponse;
import org.springframework.stereotype.Component;

@Component
public class FarePolicyResponseMapper {


    public FarePolicyResponse toResponse(FarePolicyCreateResult result) {
        return new FarePolicyResponse(
                result.policyId(),
                result.policyType(),
                result.message()
        );
    }

}
