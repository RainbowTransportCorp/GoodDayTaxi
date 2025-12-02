package com.gooddaytaxi.trip.application.port.out;

import com.gooddaytaxi.trip.domain.model.FarePolicy;

public interface CreateFarePolicyPort {
    FarePolicy create(FarePolicy farePolicy);
}
