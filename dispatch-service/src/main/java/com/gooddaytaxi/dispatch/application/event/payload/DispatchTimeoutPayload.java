package com.gooddaytaxi.dispatch.application.event.payload;

import java.time.LocalDateTime;
import java.util.UUID;

public record DispatchTimeoutPayload(
        UUID dispatchId,
        LocalDateTime timeoutAt
) {}
