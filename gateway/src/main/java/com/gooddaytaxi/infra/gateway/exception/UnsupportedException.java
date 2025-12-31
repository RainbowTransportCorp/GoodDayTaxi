package com.gooddaytaxi.infra.gateway.exception;

import lombok.Getter;

@Getter
public class UnsupportedException extends RuntimeException {

    private final TokenErrorCode errorCode;

    public UnsupportedException(TokenErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}