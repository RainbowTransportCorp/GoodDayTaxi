package com.gooddaytaxi.trip.presentation.mapper.result;

import com.gooddaytaxi.trip.application.result.TripStartResult;
import com.gooddaytaxi.trip.presentation.dto.response.TripStartResponse;
import org.springframework.stereotype.Component;

@Component
public class TripStartResponseMapper {
    public TripStartResponse toResponse(TripStartResult result) {
        return new TripStartResponse(
                result.tripId(),
                result.status(),
                result.startTime(),
                result.message()
        );
    }
}
