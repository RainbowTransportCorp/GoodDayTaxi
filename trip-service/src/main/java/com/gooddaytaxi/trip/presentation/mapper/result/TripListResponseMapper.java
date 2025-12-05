package com.gooddaytaxi.trip.presentation.mapper.result;

import com.gooddaytaxi.trip.application.result.TripItem;
import com.gooddaytaxi.trip.application.result.TripListResult;
import com.gooddaytaxi.trip.presentation.dto.response.TripListResponse;
import com.gooddaytaxi.trip.presentation.dto.response.TripResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TripListResponseMapper {

    public TripListResponse toResponse(TripListResult result) {
        List<TripResponse> responses = result.trips().stream()
                .map(this::toTripResponse)
                .toList();

        return new TripListResponse(responses);
    }

    private TripResponse toTripResponse(TripItem item) {
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
