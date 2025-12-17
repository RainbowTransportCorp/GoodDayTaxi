package com.gooddaytaxi.dispatch.application.event;

public enum DispatchEventMetadata {

    DISPATCH_CANCELED(
            "DISPATCH_CANCELED",
            "dispatch.canceled",
            "Dispatch",
            1
    ),

    DISPATCH_ACCEPTED(
            "DISPATCH_ACCEPTED",
            "dispatch.accepted",
            "Dispatch",
            1
    ),

    DISPATCH_REJECTED(
            "DISPATCH_REJECTED",
            "dispatch.rejected",
            "Dispatch",
            1
    ),

    DISPATCH_REQUESTED(
            "DISPATCH_REQUESTED",
            "dispatch.requested",
            "Dispatch",
            1
    ),

    DISPATCH_TIMEOUT(
            "DISPATCH_TIMEOUT",
            "dispatch.timeout",
            "Dispatch",
            1
    ),

    TRIP_CREATE_REQUEST(
            "TRIP_CREATE_REQUEST",
            "trip.create.request",
            "Dispatch",
            1
    );

    public final String eventType;
    public final String topic;
    public final String aggregateType;
    public final int version;

    DispatchEventMetadata(String eventType, String topic, String aggregateType, int version) {
        this.eventType = eventType;
        this.topic = topic;
        this.aggregateType = aggregateType;
        this.version = version;
    }

    public String eventType() { return eventType; }
    public String topic() { return topic; }
    public String aggregateType() { return aggregateType; }
    public int version() { return version; }
}