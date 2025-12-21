package com.gooddaytaxi.trip.presentation.mapper.result;

import com.gooddaytaxi.trip.application.result.TripLocationUpdatedResult;
import com.gooddaytaxi.trip.presentation.dto.response.TripLocationUpdatedResponse;
import org.springframework.stereotype.Component;

@Component
public class TripLocationUpdatedResponseMapper {

    public TripLocationUpdatedResponse toResponse(TripLocationUpdatedResult result) {
        return new TripLocationUpdatedResponse(
                result.tripId(),
                result.eventPublished(),
                result.sequence(),
                result.previousRegion(),
                result.currentRegion()
        );
    }
}