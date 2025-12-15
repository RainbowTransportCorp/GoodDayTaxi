package com.gooddaytaxi.support.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Metadata {
    private UUID eventId;
    private String eventType;
    private LocalDateTime occuredAt;
}
