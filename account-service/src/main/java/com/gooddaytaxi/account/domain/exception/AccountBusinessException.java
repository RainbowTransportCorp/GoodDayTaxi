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
        if (errorCode instanceof AccountErrorCode) {
            AccountErrorCode accountError = (AccountErrorCode) errorCode;
            return switch (accountError) {
                case USER_NOT_FOUND -> com.gooddaytaxi.common.core.exception.ErrorCode.INVALID_INPUT_VALUE;
                case DUPLICATE_EMAIL -> com.gooddaytaxi.common.core.exception.ErrorCode.INVALID_INPUT_VALUE;
                case DUPLICATE_VEHICLE_NUMBER -> com.gooddaytaxi.common.core.exception.ErrorCode.INVALID_INPUT_VALUE;
                case INVALID_CREDENTIALS -> com.gooddaytaxi.common.core.exception.ErrorCode.AUTH_TOKEN_MISSING;
                case INVALID_REFRESH_TOKEN -> com.gooddaytaxi.common.core.exception.ErrorCode.AUTH_TOKEN_MISSING;
                case EXPIRED_REFRESH_TOKEN -> com.gooddaytaxi.common.core.exception.ErrorCode.AUTH_TOKEN_MISSING;
                case ACCESS_DENIED -> com.gooddaytaxi.common.core.exception.ErrorCode.ACCESS_DENIED;
                case INVALID_INPUT_VALUE -> com.gooddaytaxi.common.core.exception.ErrorCode.INVALID_INPUT_VALUE;
                case ACCOUNT_LOCKED -> com.gooddaytaxi.common.core.exception.ErrorCode.ACCESS_DENIED;
                case ACCOUNT_SUSPENDED -> com.gooddaytaxi.common.core.exception.ErrorCode.ACCESS_DENIED;
                case PROFILE_UPDATE_RESTRICTED -> com.gooddaytaxi.common.core.exception.ErrorCode.ACCESS_DENIED;
                case MISSING_VEHICLE_INFO -> com.gooddaytaxi.common.core.exception.ErrorCode.INVALID_INPUT_VALUE;
                case DRIVER_LICENSE_EXPIRED -> com.gooddaytaxi.common.core.exception.ErrorCode.INVALID_INPUT_VALUE;
                case VEHICLE_REGISTRATION_REQUIRED -> com.gooddaytaxi.common.core.exception.ErrorCode.INVALID_INPUT_VALUE;
                case SLACK_ID_REQUIRED -> com.gooddaytaxi.common.core.exception.ErrorCode.INVALID_INPUT_VALUE;
            };
        }
        
        return com.gooddaytaxi.common.core.exception.ErrorCode.INTERNAL_SERVER_ERROR;
    }
    
    public ErrorCode getAccountErrorCode() {
        return accountErrorCode;
    }
}