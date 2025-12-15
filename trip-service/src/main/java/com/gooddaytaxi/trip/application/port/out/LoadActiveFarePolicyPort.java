package com.gooddaytaxi.trip.application.port.out;

import com.gooddaytaxi.trip.domain.model.FarePolicy;

public interface LoadActiveFarePolicyPort {
    FarePolicy loadActivePolicy();
}
