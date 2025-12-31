package com.gooddaytaxi.trip.presentation.mapper.result;

import com.gooddaytaxi.trip.application.result.FarePolicyCreateResult;
import com.gooddaytaxi.trip.application.result.FarePolicyItem;
import com.gooddaytaxi.trip.presentation.dto.response.CreateFarePolicyResponse;
import com.gooddaytaxi.trip.presentation.dto.response.FarePolicyResponse;
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

    public FarePolicyResponse toResponse(FarePolicyItem item) {
        return new FarePolicyResponse( item.policyId(),
                item.policyType().name(),
                item.baseDistance(),
                item.baseFare(),
                item.distRateKm(),
                item.timeRate() ); }

}
