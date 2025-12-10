package com.gooddaytaxi.account.domain.exception;

/**
 * Account 서비스용 BusinessException
 */
public class AccountBusinessException extends RuntimeException {
    
    private final AccountErrorCode accountErrorCode;
    
    public AccountBusinessException(AccountErrorCode errorCode) {
        super(errorCode.getMessage());
        this.accountErrorCode = errorCode;
    }
    
    public AccountBusinessException(AccountErrorCode errorCode, Object... args) {
        super(String.format(errorCode.getMessage(), args));
        this.accountErrorCode = errorCode;
    }
    
    public AccountBusinessException(AccountErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.accountErrorCode = errorCode;
    }
    
    public AccountBusinessException(AccountErrorCode errorCode, Throwable cause, Object... args) {
        super(String.format(errorCode.getMessage(), args), cause);
        this.accountErrorCode = errorCode;
    }
    
    public AccountErrorCode getAccountErrorCode() {
        return accountErrorCode;
    }
}