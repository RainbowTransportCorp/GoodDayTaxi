package com.gooddaytaxi.account.domain.repository;

import com.gooddaytaxi.account.domain.model.DriverProfile;

import java.util.Optional;

/**
 * 기사 프로필 도메인 리포지토리
 */
public interface DriverProfileRepository {
    
    /**
     * 기사 프로필 정보 저장
     *
     * @param driverProfile 저장할 기사 프로필 엔티티
     * @return 저장된 기사 프로필 엔티티
     */
    DriverProfile save(DriverProfile driverProfile);
    
    /**
     * 사용자 ID로 기사 프로필 조회
     *
     * @param userId 조회할 사용자 ID
     * @return 기사 프로필 정보, 존재하지 않으면 empty Optional
     */
    Optional<DriverProfile> findByUserId(String userId);
    
    /**
     * 차량번호로 기사 프로필 조회
     *
     * @param vehicleNumber 조회할 차량번호
     * @return 기사 프로필 정보, 존재하지 않으면 empty Optional
     */
    Optional<DriverProfile> findByVehicleNumber(String vehicleNumber);
    
    /**
     * 차량번호 존재 여부 확인
     *
     * @param vehicleNumber 확인할 차량번호
     * @return 차량번호가 존재하면 true, 아니면 false
     */
    boolean existsByVehicleNumber(String vehicleNumber);
    
    /**
     * 기사 프로필 정보 삭제
     *
     * @param driverProfile 삭제할 기사 프로필 엔티티
     */
    void delete(DriverProfile driverProfile);
}