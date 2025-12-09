package com.gooddaytaxi.trip.presentation.mapper.result;

import com.gooddaytaxi.trip.application.result.TripEndResult;
import com.gooddaytaxi.trip.presentation.dto.response.TripEndResponse;
import org.springframework.stereotype.Component;

@Component
public class TripEndResponseMapper {
    public TripEndResponse toResponse(TripEndResult result) {
        return new TripEndResponse(
                result.tripId(),
                result.status(),
                result.endTime(),
                result.finalFare(),
                result.message()
        );
    }
}
