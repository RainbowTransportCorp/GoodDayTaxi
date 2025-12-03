package com.gooddaytaxi.trip.application.port.out;

import com.gooddaytaxi.trip.domain.model.FarePolicy;

import java.util.List;

public interface LoadFarePoliciesPort {
    List<FarePolicy> findAll();
}
