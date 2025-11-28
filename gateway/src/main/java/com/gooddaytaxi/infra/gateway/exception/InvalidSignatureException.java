package com.gooddaytaxi.infra.gateway.exception;

import lombok.Getter;

@Getter
public class InvalidSignatureException extends RuntimeException {

    private final TokenErrorCode errorCode;

    public InvalidSignatureException(TokenErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
