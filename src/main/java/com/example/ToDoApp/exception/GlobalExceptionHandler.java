package com.example.ToDoApp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Intercepteur global simplifié des exceptions pour l'API
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Extrait le chemin de la requête en supprimant le préfixe "uri="
     */
    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
    
    /**
     * Gère les exceptions métier personnalisées
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(BusinessException ex, WebRequest request) {
        HttpStatus status = getStatusForException(ex);
        
        ApiError error = new ApiError(
            status.value(), 
            ex.getMessage(),
            extractPath(request)
        );
        
        log.error("Erreur business: {}, code: {}", ex.getMessage(), ex.getErrorCode());
        
        return new ResponseEntity<>(error, status);
    }
    
    /**
     * Détermine le statut HTTP approprié pour l'exception
     */
    private HttpStatus getStatusForException(BusinessException ex) {
        return switch (ex.getErrorCode()) {
            case TASK_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case ACCESS_DENIED -> HttpStatus.FORBIDDEN;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
    
    /**
     * Gère les exceptions de validation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });
        
        ApiError error = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            "Erreur de validation des données",
            extractPath(request)
        );
        
        log.warn("Erreur de validation: {}", validationErrors);
        
        return new ResponseEntity<>(Map.of("error", error, "validationErrors", validationErrors), HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Capture toutes les autres exceptions non gérées
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllExceptions(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        
        ApiError error = new ApiError(
            status.value(),
            "Une erreur interne est survenue",
            extractPath(request)
        );
        
        log.error("Erreur non gérée: ", ex);
        
        return new ResponseEntity<>(error, status);
    }
} 