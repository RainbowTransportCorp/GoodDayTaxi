package com.gooddaytaxi.trip.presentation.dto.response;

import java.util.List;
import java.util.UUID;

public record DriverTripHistoryResponse(
        UUID driverId,
        long totalCount,
        int page,
        int size,
        List<TripHistoryItemResponse> trips
) {
}
