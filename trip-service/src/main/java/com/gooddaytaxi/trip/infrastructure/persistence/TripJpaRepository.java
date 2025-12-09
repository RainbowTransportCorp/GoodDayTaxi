package com.gooddaytaxi.trip.infrastructure.persistence;

import com.gooddaytaxi.trip.domain.model.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TripJpaRepository extends JpaRepository<Trip, UUID> {

    Page<Trip> findByPassengerId(UUID passengerId, Pageable pageable);


}
