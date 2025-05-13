package com.example.ToDoApp.exception;

/**
 * Exception de base pour toutes les exceptions métier
 */
public class BusinessException extends RuntimeException {
    
    private final String code;
    
    public BusinessException(String message) {
        this(message, "BUSINESS_ERROR");
    }
    
    public BusinessException(String message, String code) {
        super(message);
        this.code = code;
    }
    
    public BusinessException(String message, Throwable cause) {
        this(message, "BUSINESS_ERROR", cause);
    }
    
    public BusinessException(String message, String code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
} 