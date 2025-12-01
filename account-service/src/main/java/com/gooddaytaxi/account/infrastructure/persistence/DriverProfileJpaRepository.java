package com.gooddaytaxi.account.infrastructure.persistence;

import com.gooddaytaxi.account.domain.model.DriverProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 기사 프로필 Spring Data JPA 리포지토리
 */
public interface DriverProfileJpaRepository extends JpaRepository<DriverProfile, String> {
    
    Optional<DriverProfile> findByUserId(String userId);
    
    Optional<DriverProfile> findByVehicleNumber(String vehicleNumber);
    
    boolean existsByVehicleNumber(String vehicleNumber);
}