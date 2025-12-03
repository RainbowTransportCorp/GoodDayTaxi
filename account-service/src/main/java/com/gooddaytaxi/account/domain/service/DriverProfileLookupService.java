package com.gooddaytaxi.account.domain.service;

import com.gooddaytaxi.account.domain.model.DriverProfile;
import com.gooddaytaxi.account.domain.model.User;

import java.util.UUID;

public interface DriverProfileLookupService {
    
    User findDriverByUuid(UUID driverId);
    
    DriverProfile findDriverProfileByUserId(UUID userId);
}