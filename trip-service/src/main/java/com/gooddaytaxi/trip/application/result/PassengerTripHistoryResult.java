package com.gooddaytaxi.trip.application.result;

import java.util.List;
import java.util.UUID;

public record PassengerTripHistoryResult(
        UUID passengerId,
        long totalCount,
        int page,
        int size,
        List<TripHistoryItem> trips
) {
}
