package com.gooddaytaxi.support.adapter.in.kafka.dto;

import java.util.UUID;

/** 이벤트 Payload 수신 DTO
 */
public record Payload(
    String message,
    UUID driverId,
    UUID passengerId,
    UUID notifierId,
    UUID notificationOriginId,
    String pickupAddress,
    String destinationAddress
) {}