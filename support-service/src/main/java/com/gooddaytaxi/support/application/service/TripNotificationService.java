package com.gooddaytaxi.support.application.service;

import com.gooddaytaxi.support.application.port.in.trip.EndTripUsecase;
import com.gooddaytaxi.support.application.port.in.trip.StartTripUsecase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Trip 알림 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class TripNotificationService implements StartTripUsecase, EndTripUsecase {
}
