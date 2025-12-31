package com.gooddaytaxi.trip.application.result;

import java.util.UUID;

public record TripLocationUpdatedResult(UUID tripId,
                                        boolean eventPublished,
                                        long sequence,
                                        String previousRegion,
                                        String currentRegion) {
}
