package com.gooddaytaxi.trip.infrastructure.persistence;

import com.gooddaytaxi.trip.domain.model.FarePolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FarePolicyJpaRepository extends JpaRepository<FarePolicy, UUID> {

    Optional<FarePolicy> findByIsActiveTrue();
}
