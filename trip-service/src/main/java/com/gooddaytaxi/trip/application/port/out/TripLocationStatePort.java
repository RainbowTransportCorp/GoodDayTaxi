package com.gooddaytaxi.trip.application.port.out;

import java.util.UUID;

public interface TripLocationStatePort {

    record NextSequenceResult(
            String previousRegion,
            String currentRegion,
            long nextSequence,
            boolean changed
    ) {}

    NextSequenceResult computeNext(UUID tripId, String currentRegion);
}
