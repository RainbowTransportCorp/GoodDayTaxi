package com.gooddaytaxi.trip.application.service;

import com.gooddaytaxi.trip.application.command.FarePolicyCreateCommand;
import com.gooddaytaxi.trip.application.port.out.CreateFarePolicyPort;
import com.gooddaytaxi.trip.application.port.out.LoadFarePoliciesPort;
import com.gooddaytaxi.trip.application.result.FarePolicyCreateResult;
import com.gooddaytaxi.trip.application.result.FarePolicyItem;
import com.gooddaytaxi.trip.application.result.FarePolicyListResult;
import com.gooddaytaxi.trip.domain.model.FarePolicy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FarePolicyService {

    private final CreateFarePolicyPort createFarePolicyPort;
    private final LoadFarePoliciesPort loadFarePoliciesPort;

    @Transactional
    public FarePolicyCreateResult createFarePolicy(FarePolicyCreateCommand command) {
        FarePolicy farePolicy = FarePolicy.create(
                command.policyType(),
                command.baseDistance(), // (2) Double baseDistance
                command.baseFare(),     // (3) Long baseFare
                command.distRateKm(),
                command.timeRate()
        );

        FarePolicy createdPolicy = createFarePolicyPort.create(farePolicy);

        return new FarePolicyCreateResult(
                createdPolicy.getPolicyId(),
                createdPolicy.getPolicyType().name(), // PolicyType ENUM을 String으로 변환
                "요금 정책이 성공적으로 생성되었습니다."
        );
    }

    public FarePolicyListResult getAllPolicies() {
        List<FarePolicy> policies = loadFarePoliciesPort.findAll();

        List<FarePolicyItem> items = policies.stream()
                .map(p -> new FarePolicyItem(
                        p.getPolicyId(),
                        p.getPolicyType(),
                        p.getBaseDistance(),
                        p.getBaseFare(),
                        p.getDistRateKm(),
                        p.getTimeRate()
                ))
                .toList();

        return new FarePolicyListResult(items);
    }

}
