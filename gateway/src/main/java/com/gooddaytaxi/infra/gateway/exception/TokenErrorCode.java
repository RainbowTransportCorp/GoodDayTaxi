package com.gooddaytaxi.infra.gateway.exception;

import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenErrorCode {

    TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "T000", "Authorization 헤더가 없거나 잘못된 형식입니다."),
    INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "T001", "유효하지 않은 서명입니다."),
    EXPIRED(HttpStatus.UNAUTHORIZED, "T002", "만료된 토큰입니다."),
    MALFORMED(HttpStatus.BAD_REQUEST, "T003", "잘못된 JWT 형식입니다."),
    UNSUPPORTED(HttpStatus.BAD_REQUEST, "T004", "지원하지 않는 토큰 형식입니다."),
    INVALID_CLAIMS(HttpStatus.BAD_REQUEST, "T005", "클레임이 비어 있거나 잘못되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
