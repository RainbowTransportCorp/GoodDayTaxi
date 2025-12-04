package com.gooddaytaxi.trip.application.port.out;

import com.gooddaytaxi.trip.domain.model.FarePolicy;

import java.util.List;
import java.util.UUID;

public interface CreateFarePolicyPort {
    FarePolicy create(FarePolicy farePolicy);

}
