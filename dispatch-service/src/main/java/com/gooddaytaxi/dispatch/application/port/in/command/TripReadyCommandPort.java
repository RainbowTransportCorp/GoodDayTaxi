package com.gooddaytaxi.dispatch.application.port.in.command;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TripReadyCommandPort {

    void onTripReady(UUID dispatchId,UUID tripId, LocalDateTime createdAt);

}
