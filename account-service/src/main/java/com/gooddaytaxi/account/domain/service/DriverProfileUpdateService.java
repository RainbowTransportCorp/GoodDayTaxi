package com.gooddaytaxi.account.domain.service;

import com.gooddaytaxi.account.domain.model.DriverProfile;

public interface DriverProfileUpdateService {
    
    void updateVehicleInfo(DriverProfile driverProfile, String vehicleNumber, String vehicleType, String vehicleColor);
    
    void validateDriverProfileUpdate(String vehicleNumber, String vehicleType, String vehicleColor);
}