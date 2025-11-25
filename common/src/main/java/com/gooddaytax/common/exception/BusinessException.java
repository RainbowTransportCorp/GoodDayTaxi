package com.gooddaytax.common.exception;

import lombok.Getter;

/**
 * 비즈니스 로직 처리 중 발생하는 예외
 *
 * ErrorCode enum을 사용하여 일관된 예외 처리를 제공합니다.
 * 단일 책임 원칙(SRP)에 따라 비즈니스 예외 표현만 담당합니다.
 * 리스코프 치환 원칙(LSP)을 준수하여 RuntimeException을 완전히 대체할 수 있습니다.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] args;

    /**
     * 에러 코드로 비즈니스 예외 생성
     *
     * @param errorCode 에러 코드
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = null;
    }

    /**
     * 에러 코드와 메시지 인수로 비즈니스 예외 생성
     *
     * @param errorCode 에러 코드
     * @param args 메시지 포맷 인수
     */
    public BusinessException(ErrorCode errorCode, Object... args) {
        super(String.format(errorCode.getMessage(), args));
        this.errorCode = errorCode;
        this.args = args;
    }

    /**
     * 에러 코드와 원인 예외로 비즈니스 예외 생성
     *
     * @param errorCode 에러 코드
     * @param cause 원인 예외
     */
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.args = null;
    }

    /**
     * 에러 코드, 메시지 인수, 원인 예외로 비즈니스 예외 생성
     *
     * @param errorCode 에러 코드
     * @param cause 원인 예외
     * @param args 메시지 포맷 인수
     */
    public BusinessException(ErrorCode errorCode, Throwable cause, Object... args) {
        super(String.format(errorCode.getMessage(), args), cause);
        this.errorCode = errorCode;
        this.args = args;
    }
}