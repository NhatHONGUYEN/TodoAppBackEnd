package com.example.ToDoApp.exception;

/**
 * Exception de base pour toutes les exceptions métier
 */
public class BusinessException extends RuntimeException {
    
    private final ErrorCode code;
    
    public BusinessException(String message) {
        this(message, ErrorCode.BUSINESS_ERROR);
    }
    
    public BusinessException(String message, ErrorCode code) {
        super(message);
        this.code = code;
    }
    
    public BusinessException(ErrorCode code) {
        super(code.getDefaultMessage());
        this.code = code;
    }
    
    public BusinessException(String message, Throwable cause) {
        this(message, ErrorCode.BUSINESS_ERROR, cause);
    }
    
    public BusinessException(String message, ErrorCode code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
    
    public ErrorCode getErrorCode() {
        return code;
    }
    
    public String getCode() {
        return code.name();
    }
} 