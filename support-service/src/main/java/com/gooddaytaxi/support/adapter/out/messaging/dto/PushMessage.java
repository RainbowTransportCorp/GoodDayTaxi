package com.gooddaytaxi.support.adapter.out.messaging.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

/**
 * Push 메시지 DTO
 * RabbitMQ로 Push하는 메시지
 */
public record PushMessage (
        List<UUID> receivers,
        String title,
        String body
) {

    private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public static PushMessage from(String data) {
        try {
            return mapper.readValue(data, PushMessage.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON for PushMessage", e);
        }
    }
}