package com.gooddaytaxi.account.domain.exception;

import com.gooddaytaxi.common.core.exception.ErrorLevel;
import lombok.RequiredArgsConstructor;

/**
 * common-core ErrorCode를 account 서비스의 ErrorCode 인터페이스에 맞게 래핑
 */
@RequiredArgsConstructor
public class CommonErrorCode implements ErrorCode {
    
    private final com.gooddaytaxi.common.core.exception.ErrorCode commonErrorCode;
    
    @Override
    public ErrorLevel getLevel() {
        return commonErrorCode.getLevel();
    }
    
    @Override
    public String getCode() {
        return commonErrorCode.getCode();
    }
    
    @Override
    public String getMessage() {
        return commonErrorCode.getMessage();
    }
    
    /**
     * common ErrorCode를 account ErrorCode로 변환하는 팩토리 메소드
     */
    public static CommonErrorCode of(com.gooddaytaxi.common.core.exception.ErrorCode commonErrorCode) {
        return new CommonErrorCode(commonErrorCode);
    }
    
    /**
     * 내부 common ErrorCode 반환 (AccountBusinessException에서 사용)
     */
    public com.gooddaytaxi.common.core.exception.ErrorCode getCommonErrorCode() {
        return commonErrorCode;
    }
}