package com.gooddaytaxi.infra.gateway.exception;

import lombok.Getter;

@Getter
public class MalFormedException extends RuntimeException {

    private final TokenErrorCode errorCode;

    public MalFormedException(TokenErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}