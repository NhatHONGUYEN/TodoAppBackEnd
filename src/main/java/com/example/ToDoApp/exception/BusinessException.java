package com.example.ToDoApp.exception;

/**
 * Exception de base simplifiée pour toutes les erreurs métier
 */
public class BusinessException extends RuntimeException {
    
    private final ErrorCode code;
    
    public BusinessException(String message, ErrorCode code) {
        super(message);
        this.code = code;
    }
    
    public ErrorCode getErrorCode() {
        return code;
    }
} 