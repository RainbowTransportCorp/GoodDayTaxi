package com.gooddaytaxi.trip.application.result;

import java.util.List;

public record TripListResult(
        List<TripItem> trips
) {
}
