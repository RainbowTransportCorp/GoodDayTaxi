package com.gooddaytaxi.trip.presentation.mapper.command;

import com.gooddaytaxi.trip.application.command.TripCreateCommand;
import com.gooddaytaxi.trip.presentation.dto.request.CreateTripRequest;
import org.springframework.stereotype.Component;

@Component
public class TripCreateRequestMapper {

    public TripCreateCommand toCommand(CreateTripRequest request) {
        return new TripCreateCommand(
                request.policyId(),
                request.passengerId(),
                request.driverId(),
                request.dispatchId(),
                request.pickupAddress(),
                request.destinationAddress()
        );
    }

}
