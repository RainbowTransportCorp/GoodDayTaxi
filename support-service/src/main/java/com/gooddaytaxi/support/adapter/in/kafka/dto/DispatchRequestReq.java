package com.gooddaytaxi.support.adapter.in.kafka.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

/**
 * 배차 콜 요청 DTO - 클라이언트로부터 수신한 요청 정보를 담는 객체
 * 배차 수락 DTO - 클라이언트로부터 수락된 배차 정보를 담는 객체
 */
public record DispatchRequestReq(
        UUID eventId,
        String eventType,
        String occurredAt,
        Integer payloadVersion,
        Payload payload
) {

    private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public static DispatchRequestReq from(String data) {
        try {
            return mapper.readValue(data, DispatchRequestReq.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON for DispatchRequestReq", e);
        }
    }
}
