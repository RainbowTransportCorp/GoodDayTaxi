package com.gooddaytaxi.account.domain.service;

import com.gooddaytaxi.account.domain.model.User;

public interface UserProfileUpdateService {
    
    void updateProfile(User user, String name, String phoneNumber);
}