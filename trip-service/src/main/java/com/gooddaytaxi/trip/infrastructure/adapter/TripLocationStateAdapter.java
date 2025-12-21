package com.gooddaytaxi.trip.infrastructure.adapter;

import com.gooddaytaxi.trip.application.port.out.TripLocationStatePort;
import com.gooddaytaxi.trip.domain.model.TripLocationState;
import com.gooddaytaxi.trip.infrastructure.persistence.TripLocationStateJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Component
@RequiredArgsConstructor
public class TripLocationStateAdapter implements TripLocationStatePort {
    private final TripLocationStateJpaRepository repo;

    @Override
    @Transactional
    public NextSequenceResult computeNext(UUID tripId, String currentRegion) {

        TripLocationState state = repo.findByTripIdForUpdate(tripId)
                .orElseGet(() -> repo.save(TripLocationState.init(tripId, currentRegion)));

        String prev = state.getLastRegion();
        boolean changed = !prev.equals(currentRegion);

        if (!changed) {
            return new NextSequenceResult(prev, currentRegion, state.getLastSequence(), false);
        }

        long nextSeq = state.getLastSequence() + 1;
        state.update(currentRegion, nextSeq);

        return new NextSequenceResult(prev, currentRegion, nextSeq, true);
    }
}
