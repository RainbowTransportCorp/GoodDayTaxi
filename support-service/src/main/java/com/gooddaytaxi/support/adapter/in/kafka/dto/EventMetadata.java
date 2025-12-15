package com.gooddaytaxi.support.adapter.in.kafka.dto;

import com.gooddaytaxi.support.application.Metadata;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventMetadata (
        UUID eventId,
        String eventType,
        LocalDateTime occuredAt
){
    public static EventMetadata from(Metadata metadata){
        return new EventMetadata(metadata.getEventId(), metadata.getEventType(), metadata.getOccuredAt());
    }

    public Metadata to(){
        return new Metadata(this.eventId(), this.eventType(), this.occuredAt());
    }
}