package com.gooddaytaxi.support.adapter.in.kafka.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

/**
 * 배차 콜 요청 DTO
 * 클라이언트로부터 수신한 요청 정보를 담는 객체
 */
public record DispatchCallRequestReq (
    UUID notificationOriginId,
    UUID notifierId,
//    UUID dispatchId,
    UUID driverId,
    UUID passengerId,
    String pickupAddress,
    String destinationAddress,
    String message
) {

    private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public static DispatchCallRequestReq from(String data) {
        try {
            return mapper.readValue(data, DispatchCallRequestReq.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON for DispatchCallRequestReq", e);
        }
    }
}
