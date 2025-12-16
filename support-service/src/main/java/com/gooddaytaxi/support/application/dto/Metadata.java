package com.gooddaytaxi.support.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record Metadata (
    UUID eventId,
    String eventType,
    LocalDateTime occuredAt
){}
