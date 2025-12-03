package com.gooddaytaxi.support.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Command {
    private UUID notificationOriginId;
    private UUID notifierId;
    private String message;
}
