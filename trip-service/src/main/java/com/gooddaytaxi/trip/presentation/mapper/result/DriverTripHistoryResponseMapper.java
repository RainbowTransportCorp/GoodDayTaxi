package com.gooddaytaxi.trip.presentation.mapper.result;

import com.gooddaytaxi.trip.application.result.DriverTripHistoryResult;
import com.gooddaytaxi.trip.presentation.dto.response.DriverTripHistoryResponse;
import com.gooddaytaxi.trip.presentation.dto.response.TripHistoryItemResponse;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
@Component
public class DriverTripHistoryResponseMapper {

    public DriverTripHistoryResponse toResponse(DriverTripHistoryResult result) {
        return new DriverTripHistoryResponse(
                result.driverId(),
                result.totalCount(),
                result.page(),
                result.size(),
                result.trips().stream()
                        .map(t -> new TripHistoryItemResponse(
                                t.tripId(),
                                t.status(),
                                t.pickupAddress(),
                                t.destinationAddress(),
                                t.startTime(),
                                t.endTime(),
                                t.totalDistance(),
                                t.totalDuration(),
                                t.finalFare()
                        ))
                        .collect(Collectors.toList())
        );
    }
}
