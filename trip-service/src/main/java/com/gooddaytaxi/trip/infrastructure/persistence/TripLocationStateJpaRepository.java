package com.gooddaytaxi.trip.infrastructure.persistence;

import com.gooddaytaxi.trip.domain.model.TripLocationState;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TripLocationStateJpaRepository extends JpaRepository<TripLocationState, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from TripLocationState s where s.tripId = :tripId")
    Optional<TripLocationState> findByTripIdForUpdate(@Param("tripId") UUID tripId);
}
