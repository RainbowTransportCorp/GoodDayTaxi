package com.gooddaytaxi.dispatch.application.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class DispatchNotFoundException extends RuntimeException {

    private final String code = "DISPATCH_NOT_FOUND";
    private final UUID dispatchId;

    public DispatchNotFoundException(UUID dispatchId) {
        super("배차 정보를 찾을 수 없습니다. dispatchId=" + dispatchId);
        this.dispatchId = dispatchId;
    }
}

