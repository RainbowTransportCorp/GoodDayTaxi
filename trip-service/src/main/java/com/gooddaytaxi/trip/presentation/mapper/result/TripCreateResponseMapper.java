package com.gooddaytaxi.trip.presentation.mapper.result;

import com.gooddaytaxi.trip.application.result.TripCreateResult;
import com.gooddaytaxi.trip.presentation.dto.response.CreateTripResponse;
import org.springframework.stereotype.Component;

@Component
public class TripCreateResponseMapper {

    public CreateTripResponse toResponse(TripCreateResult result) {
        return new CreateTripResponse(
                result.tripId(),
                result.status(),
                result.message()
        );
    }

}
