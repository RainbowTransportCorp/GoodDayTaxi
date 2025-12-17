package com.gooddaytaxi.support.adapter.in.kafka.dto;

import com.gooddaytaxi.support.application.dto.Metadata;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventMetadata (
        UUID eventId,
        String eventType,
        LocalDateTime occurredAt
){
    public static EventMetadata from(Metadata metadata){
        return new EventMetadata(metadata.eventId(), metadata.eventType(), metadata.occurredAt());
    }

    public Metadata to(){
        return new Metadata(this.eventId(), this.eventType(), this.occurredAt());
    }
}