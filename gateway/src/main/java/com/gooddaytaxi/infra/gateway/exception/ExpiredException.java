package com.gooddaytaxi.infra.gateway.exception;

import lombok.Getter;

@Getter
public class ExpiredException extends RuntimeException {

    private final TokenErrorCode errorCode;

    public ExpiredException(TokenErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}