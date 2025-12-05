package com.gooddaytaxi.account.domain.exception;

import com.gooddaytaxi.common.core.exception.BusinessException;

/**
 * Account 서비스용 BusinessException 확장
 * 공통 ErrorCode와 AccountErrorCode 모두 지원
 */
public class AccountBusinessException extends BusinessException {
    
    private final ErrorCode accountErrorCode;
    
    public AccountBusinessException(ErrorCode errorCode) {
        super(convertToCommonErrorCode(errorCode));
        this.accountErrorCode = errorCode;
    }
    
    public AccountBusinessException(ErrorCode errorCode, Object... args) {
        super(convertToCommonErrorCode(errorCode), args);
        this.accountErrorCode = errorCode;
    }
    
    public AccountBusinessException(ErrorCode errorCode, Throwable cause) {
        super(convertToCommonErrorCode(errorCode), cause);
        this.accountErrorCode = errorCode;
    }
    
    public AccountBusinessException(ErrorCode errorCode, Throwable cause, Object... args) {
        super(convertToCommonErrorCode(errorCode), cause, args);
        this.accountErrorCode = errorCode;
    }
    
    /**
     * Account ErrorCode를 common ErrorCode로 변환
     */
    private static com.gooddaytaxi.common.core.exception.ErrorCode convertToCommonErrorCode(ErrorCode errorCode) {
        if (errorCode instanceof CommonErrorCode) {
            return ((CommonErrorCode) errorCode).getCommonErrorCode();
        }
        
        // AccountErrorCode인 경우 적절한 공통 ErrorCode로 매핑
        return com.gooddaytaxi.common.core.exception.ErrorCode.INTERNAL_SERVER_ERROR;
    }
    
    public ErrorCode getAccountErrorCode() {
        return accountErrorCode;
    }
}