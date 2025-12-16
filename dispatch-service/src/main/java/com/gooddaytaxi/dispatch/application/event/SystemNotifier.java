package com.gooddaytaxi.dispatch.application.event;

import java.util.UUID;

/**
 * System 측에서 보내는 알림 (timeout 등 관리자 측)일 경우 uuid 고정
 */
public final class SystemNotifier {
    public static final UUID SYSTEM_ID =
            UUID.fromString("99999999-9999-9999-9999-999999999999");

    private SystemNotifier() {}
}

