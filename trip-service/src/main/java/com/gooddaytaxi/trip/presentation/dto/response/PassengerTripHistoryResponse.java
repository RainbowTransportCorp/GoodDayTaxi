package com.gooddaytaxi.trip.presentation.dto.response;

import com.gooddaytaxi.trip.application.result.TripHistoryItem;

import java.util.List;
import java.util.UUID;

public record PassengerTripHistoryResponse(
        UUID passengerId,
        long totalCount,
        int page,
        int size,
        List<TripHistoryItemResponse> trips
) {
}
