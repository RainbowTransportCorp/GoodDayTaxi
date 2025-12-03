package com.gooddaytaxi.trip.presentation.mapper.result;

import com.gooddaytaxi.trip.application.result.FarePolicyCreateResult;
import com.gooddaytaxi.trip.presentation.dto.response.CreateFarePolicyResponse;
import org.springframework.stereotype.Component;

@Component
public class FarePolicyResponseMapper {


    public CreateFarePolicyResponse toResponse(FarePolicyCreateResult result) {
        return new CreateFarePolicyResponse(
                result.policyId(),
                result.policyType(),
                result.message()
        );
    }

}
