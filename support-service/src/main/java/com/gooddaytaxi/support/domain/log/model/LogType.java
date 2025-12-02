package com.gooddaytaxi.support.domain.log.model;

/**
 * 로그 타입 - 배차, 운행, 결제 에러 및 시스템 에러
 */
public enum LogType {
    DISPATCH_ERROR, // 배차 진행 장애
    TRIP_ERROR,     // 운행 진행 장애
    PAYMENT_ERROR,  // 결제 진행 장애
    SYSTEM_WARNING  // 시스템 장애
}
