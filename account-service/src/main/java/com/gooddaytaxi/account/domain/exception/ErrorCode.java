package com.gooddaytaxi.account.domain.exception;

import com.gooddaytaxi.common.core.exception.ErrorLevel;

/**
 * Account 서비스 에러 코드 인터페이스
 * common-core의 ErrorCode와 domain 특화 ErrorCode 모두 지원
 */
public interface ErrorCode {
    
    /**
     * 에러 레벨 반환
     */
    ErrorLevel getLevel();
    
    /**
     * 에러 코드 반환 
     */
    String getCode();
    
    /**
     * 에러 메시지 반환
     */
    String getMessage();
}