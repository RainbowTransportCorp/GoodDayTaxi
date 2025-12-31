package com.gooddaytaxi.trip.presentation.mapper.result;

import com.gooddaytaxi.trip.application.result.TripItem;
import com.gooddaytaxi.trip.presentation.dto.response.TripResponse;
import org.springframework.stereotype.Component;


@Component
public class TripDetailResponseMapper {

    public TripResponse toResponse(TripItem item) {
        return new TripResponse(
                item.tripId(),
                item.status(),
                item.policyId(),
                item.passengerId(),
                item.driverId(),
                item.pickupAddress(),
                item.destinationAddress(),
                item.startTime(),
                item.endTime(),
                item.totalDistance(),
                item.finalFare()
        );
    }

}
