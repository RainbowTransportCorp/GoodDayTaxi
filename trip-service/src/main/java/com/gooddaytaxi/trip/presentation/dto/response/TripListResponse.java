package com.gooddaytaxi.trip.presentation.dto.response;

import java.util.List;

public record TripListResponse(
        List<TripResponse> trips
) {
}
