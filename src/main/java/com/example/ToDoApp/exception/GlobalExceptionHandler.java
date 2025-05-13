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
 * Intercepteur global des exceptions pour l'API
 * Convertit les exceptions en réponses HTTP standardisées
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Gère les exceptions métier personnalisées
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(BusinessException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        
        // Ajuster le statut HTTP selon le type d'exception
        if (ex instanceof TaskNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex instanceof AccessDeniedException) {
            status = HttpStatus.FORBIDDEN;
        }
        
        // Créer la réponse d'erreur standardisée
        ApiError error = new ApiError(
            status.value(),
            status.getReasonPhrase(),
            ex.getCode(),
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        // Logger l'erreur
        log.error("Erreur business: {}, code: {}", ex.getMessage(), ex.getCode());
        
        return new ResponseEntity<>(error, status);
    }
    
    /**
     * Gère les exceptions de validation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ApiError error = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "VALIDATION_ERROR",
            "Erreur de validation des données d'entrée",
            request.getDescription(false).replace("uri=", "")
        );
        
        log.warn("Erreur de validation: {}", errors);
        
        return new ResponseEntity<>(Map.of("error", error, "validationErrors", errors), HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Capture toutes les autres exceptions non gérées
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllExceptions(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        
        ApiError error = new ApiError(
            status.value(),
            status.getReasonPhrase(),
            "SYSTEM_ERROR",
            "Une erreur interne est survenue",
            request.getDescription(false).replace("uri=", "")
        );
        
        // Logger l'erreur complète avec la stack trace
        log.error("Erreur non gérée: ", ex);
        
        return new ResponseEntity<>(error, status);
    }
} 