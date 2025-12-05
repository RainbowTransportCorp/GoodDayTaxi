package com.gooddaytaxi.account.infrastructure.persistence;

import com.gooddaytaxi.account.domain.model.DriverProfile;
import com.gooddaytaxi.account.domain.repository.DriverProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA 기반 기사 프로필 리포지토리 구현체
 */
@Repository
@RequiredArgsConstructor
public class JpaDriverProfileRepository implements DriverProfileRepository {

    private final DriverProfileJpaRepository driverProfileJpaRepository;

    @Override
    public DriverProfile save(DriverProfile driverProfile) {
        return driverProfileJpaRepository.save(driverProfile);
    }

    @Override
    public Optional<DriverProfile> findByUserId(UUID userId) {
        return driverProfileJpaRepository.findByUserId(userId);
    }

    @Override
    public Optional<DriverProfile> findByVehicleNumber(String vehicleNumber) {
        return driverProfileJpaRepository.findByVehicleNumber(vehicleNumber);
    }

    @Override
    public boolean existsByVehicleNumber(String vehicleNumber) {
        return driverProfileJpaRepository.existsByVehicleNumber(vehicleNumber);
    }

    @Override
    public void delete(DriverProfile driverProfile) {
        driverProfileJpaRepository.delete(driverProfile);
    }
}