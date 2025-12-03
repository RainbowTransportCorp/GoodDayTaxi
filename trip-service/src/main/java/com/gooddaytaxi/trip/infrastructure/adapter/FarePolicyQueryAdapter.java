package com.gooddaytaxi.trip.infrastructure.adapter;

import com.gooddaytaxi.trip.application.port.out.LoadFarePoliciesPort;
import com.gooddaytaxi.trip.domain.model.FarePolicy;
import com.gooddaytaxi.trip.infrastructure.persistence.FarePolicyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FarePolicyQueryAdapter implements LoadFarePoliciesPort {
    private final FarePolicyJpaRepository farePolicyJpaRepository;

    @Override
    public List<FarePolicy> findAll() {
        return farePolicyJpaRepository.findAll();
    }

}
