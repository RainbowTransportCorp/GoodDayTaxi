package com.gooddaytaxi.infra.gateway.exception;

import lombok.Getter;

@Getter
public class EmptyClaimsException extends RuntimeException {

    private final TokenErrorCode errorCode;

    public EmptyClaimsException(TokenErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}