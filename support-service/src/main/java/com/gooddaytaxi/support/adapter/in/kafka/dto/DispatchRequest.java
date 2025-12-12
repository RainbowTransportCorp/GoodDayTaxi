package com.gooddaytaxi.support.adapter.in.kafka.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.support.application.Metadata;

/**
 * 배차 콜 요청 DTO - 클라이언트로부터 수신한 요청 정보를 담는 객체
 * 배차 수락 DTO - 클라이언트로부터 수락된 배차 정보를 담는 객체
 */
public record DispatchRequest(
//        UUID eventId,
//        String eventType,
//        String occurredAt,
        Metadata metadata,
        Integer payloadVersion,
        Payload payload
) {

    private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public static DispatchRequest from(String data) {
        try {
            return mapper.readValue(data, DispatchRequest.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON for DispatchRequest", e);
        }
    }
}
