package com.gooddaytaxi.trip.application.port.out;

import com.gooddaytaxi.trip.domain.model.FarePolicy;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoadFarePoliciesPort {
    List<FarePolicy> findAll();

    Optional<FarePolicy> findById(UUID policyId);
}
