package com.gooddaytaxi.dispatch.infrastructure.client.account.dto;

import java.util.UUID;

public record DriverInfo(
    UUID driverId,
    String status
) {}
