package com.gooddaytaxi.support.adapter.in.kafka.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 배차 콜 요청 DTO - 클라이언트로부터 수신한 요청 정보를 담는 객체
 * 배차 수락 DTO - 클라이언트로부터 수락된 배차 정보를 담는 객체
 */

public record EventRequest(
//        EventMetadata eventMetadata,
        UUID eventId,
        String eventType,
        LocalDateTime occuredAt,
        Integer payloadVersion,
        Map<String, Object> payload
) {

    private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public static EventRequest fromJson(String json) {
        try {
            return mapper.readValue(json, EventRequest.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON for EventRequest", e);
        }
    }

    public <T> T convertPayload(Class<T> clazz) {
        try {
            return mapper.convertValue(payload, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Cannot convert payload to " + clazz.getSimpleName(), e);
        }
    }
}


//public record EventRequest(
//        EventMetadata eventMetadata,
//        Integer payloadVersion,
//        DispatchRequestedEventPayload payload
//) {
//
//    private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
//
//    public static EventRequest from(String data) {
//        try {
//            return mapper.readValue(data, EventRequest.class);
//        } catch (Exception e) {
//            throw new RuntimeException("Invalid JSON for EventRequest", e);
//        }
//    }
//}
