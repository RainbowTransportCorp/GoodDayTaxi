package com.gooddaytaxi.trip.presentation.mapper.result;

import com.gooddaytaxi.trip.application.result.FarePolicyCreateResult;
import com.gooddaytaxi.trip.application.result.FarePolicyItem;
import com.gooddaytaxi.trip.application.result.FarePolicyListResult;
import com.gooddaytaxi.trip.presentation.dto.request.CreateFarePolicyRequest;
import com.gooddaytaxi.trip.presentation.dto.response.CreateFarePolicyResponse;
import com.gooddaytaxi.trip.presentation.dto.response.FarePolicyListResponse;
import com.gooddaytaxi.trip.presentation.dto.response.FarePolicyResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FarePolicyListResponseMapper {
    public CreateFarePolicyResponse toCreateResponse(FarePolicyCreateResult result) {
        return new CreateFarePolicyResponse(
                result.policyId(),
                result.policyType(),
                "요금 정책 생성이 완료되었습니다."
        );
    }

    // ✅ 2) 전체 조회 결과 -> 응답 DTO 리스트
    public List<FarePolicyResponse> toListResponse(FarePolicyListResult result) {
        return result.policies().stream()
                .map(this::toPolicyResponse)
                .toList();
    }

    // ✅ 3) Item -> 한 건 응답 DTO
    private FarePolicyResponse toPolicyResponse(FarePolicyItem item) {
        return new FarePolicyResponse(
                item.policyId(),
                item.policyType().name(),
                item.baseDistance(),
                item.baseFare(),
                item.distRateKm(),
                item.timeRate()
        );
    }
}
