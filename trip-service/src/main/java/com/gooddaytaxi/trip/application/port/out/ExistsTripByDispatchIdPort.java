package com.gooddaytaxi.trip.application.port.out;

import java.util.UUID;

public interface ExistsTripByDispatchIdPort {
    boolean existsByDispatchId(UUID dispatchId);
}
