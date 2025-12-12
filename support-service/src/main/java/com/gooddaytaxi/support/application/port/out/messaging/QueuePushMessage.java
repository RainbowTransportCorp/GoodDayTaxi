package com.gooddaytaxi.support.application.port.out.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.support.application.Metadata;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * Push 메시지 DTO
 * - RabbitMQ로 Push하는 메시지
 * - receivers 리스트 index: 0 -> driverId, 1 -> passengerId
 */

public record QueuePushMessage(
        List<UUID> receivers,
        Metadata metadata,
        String title,
        String body
) implements Serializable {

    private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public static QueuePushMessage from(String data) {
        try {
            return mapper.readValue(data, QueuePushMessage.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON for QueuePushMessage", e);
        }
    }

    // 필요하다면 팩토리 메서드
    public static QueuePushMessage create(List<UUID> receivers, Metadata metadata, String title, String body) {
        return new QueuePushMessage(receivers, metadata, title, body);
    }
}
