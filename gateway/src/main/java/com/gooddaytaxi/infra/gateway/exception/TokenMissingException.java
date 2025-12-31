package com.gooddaytaxi.infra.gateway.exception;

public class TokenMissingException extends RuntimeException {

    private final TokenErrorCode errorCode;

    public TokenMissingException(TokenErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
