package com.gooddaytaxi.trip.presentation.mapper.command;

import com.gooddaytaxi.trip.application.command.EndTripCommand;
import com.gooddaytaxi.trip.presentation.dto.request.EndTripRequest;
import org.springframework.stereotype.Component;

@Component
public class EndTripRequestMapper {
    public EndTripCommand toCommand(EndTripRequest request) {
        return new EndTripCommand(
                request.totalDistance(),
                request.totalDuration()
        );
    }
}
