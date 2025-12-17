package com.gooddaytaxi.trip.presentation.mapper.command;

import com.gooddaytaxi.trip.application.command.EndTripCommand;
import com.gooddaytaxi.trip.presentation.dto.request.EndTripRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EndTripRequestMapper {
    public EndTripCommand toCommand(EndTripRequest request, UUID finalNotifierId) {
        return new EndTripCommand(
                finalNotifierId,
                request.totalDistance(),
                request.totalDuration()
        );
    }
}
